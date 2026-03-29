package com.team695.scoutifyapp

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.database.sqlite.SQLiteDatabase
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.team695.scoutifyapp.data.api.NetworkMonitor
import com.team695.scoutifyapp.data.api.NetworkMonitorStatus
import com.team695.scoutifyapp.data.api.client.CasdoorClient
import com.team695.scoutifyapp.data.api.client.GithubClient
import com.team695.scoutifyapp.data.api.client.ScoutifyClient
import com.team695.scoutifyapp.data.api.model.Asset
import com.team695.scoutifyapp.data.api.model.GameConstantsStore
import com.team695.scoutifyapp.data.api.model.GithubResponse
import com.team695.scoutifyapp.data.api.model.User
import com.team695.scoutifyapp.data.api.service.AppService
import com.team695.scoutifyapp.data.api.service.CommentService
import com.team695.scoutifyapp.data.api.service.LoginService
import com.team695.scoutifyapp.data.api.service.MatchService
import com.team695.scoutifyapp.ui.theme.ScoutifyTheme
import com.team695.scoutifyapp.data.api.service.TaskService
import com.team695.scoutifyapp.data.repository.MatchRepository
import com.team695.scoutifyapp.data.repository.TaskRepository
import com.team695.scoutifyapp.data.repository.UserRepository
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.db.GameDetailsEntity
import com.team695.scoutifyapp.db.CommentsEntity
import com.team695.scoutifyapp.data.api.service.GameDetailsService
import com.team695.scoutifyapp.data.api.service.TeamNameService
import com.team695.scoutifyapp.data.api.service.UserService
import com.team695.scoutifyapp.data.charAdapter
import com.team695.scoutifyapp.data.intAdapter
import com.team695.scoutifyapp.data.repository.CommentRepository
import com.team695.scoutifyapp.data.repository.GameDetailRepository
import com.team695.scoutifyapp.data.repository.TeamNameRepository
import com.team695.scoutifyapp.data.repository.PitScoutingRepository
import com.team695.scoutifyapp.data.update.UpdateManager
import com.team695.scoutifyapp.data.update.UpdateReceiver
import com.team695.scoutifyapp.db.GameConstantsEntity
import com.team695.scoutifyapp.ui.extensions.androidID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // TODO: Add notification to say permission is granted
        } else {
            // TODO: Add notification to say permission is not granted
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MAIN", "Android_ID: ${applicationContext.androidID}")

        ScoutifyClient.initialize(applicationContext)

        val dbName = "scoutify_test.db" // Using a test name to avoid messing up real data

        val driver = AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = applicationContext,
            name = dbName,
            callback = object : AndroidSqliteDriver.Callback(AppDatabase.Schema) {
                /**
                 * Allows concurrent read and write; prevents SQLite DB lock
                 */
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)

                    val busyTimeout = 5000;

                    db.query("PRAGMA journal_mode=WAL;").use {
                        it.moveToFirst()
                    }

                    db.query("PRAGMA busy_timeout=$busyTimeout;").use {
                        it.moveToFirst()
                    }
                }
            }
        )

        val db = AppDatabase(
            driver = driver,
            gameDetailsEntityAdapter = GameDetailsEntity.Adapter(
                idAdapter = intAdapter,
                task_idAdapter = intAdapter,
                match_numberAdapter = intAdapter,
                allianceAdapter = charAdapter,
                alliance_positionAdapter = intAdapter,
                auton_fuel_countAdapter = intAdapter,
                transition_cycling_timeAdapter = intAdapter,
                transition_stockpiling_timeAdapter = intAdapter,
                transition_defending_timeAdapter = intAdapter,
                transition_broken_timeAdapter = intAdapter,
                shift1_cycling_timeAdapter = intAdapter,
                shift1_stockpiling_timeAdapter = intAdapter,
                shift1_defending_timeAdapter = intAdapter,
                shift1_broken_timeAdapter = intAdapter,
                shift2_cycling_timeAdapter = intAdapter,
                shift2_stockpiling_timeAdapter = intAdapter,
                shift2_defending_timeAdapter = intAdapter,
                shift2_broken_timeAdapter = intAdapter,
                shift3_cycling_timeAdapter = intAdapter,
                shift3_stockpiling_timeAdapter = intAdapter,
                shift3_defending_timeAdapter = intAdapter,
                shift3_broken_timeAdapter = intAdapter,
                shift4_cycling_timeAdapter = intAdapter,
                shift4_stockpiling_timeAdapter = intAdapter,
                shift4_defending_timeAdapter = intAdapter,
                shift4_broken_timeAdapter = intAdapter,
                endgame_cycling_timeAdapter = intAdapter,
                endgame_stockpiling_timeAdapter = intAdapter,
                endgame_defending_timeAdapter = intAdapter,
                endgame_broken_timeAdapter = intAdapter,
                teleop_fuel_countAdapter = intAdapter,
            ),
            commentsEntityAdapter = CommentsEntity.Adapter(
                match_numberAdapter = intAdapter,
                team_numberAdapter  = intAdapter,
                alliance_positionAdapter = intAdapter,
                submittedAdapter = intAdapter
            ),
            gameConstantsEntityAdapter = GameConstantsEntity.Adapter(
                frc_season_master_sm_yearAdapter = intAdapter,
                game_matchup_gm_game_typeAdapter = charAdapter
            )
        )

        db.gameConstantsQueries.seedData()

        GameConstantsStore.update(
            db.gameConstantsQueries.getConstants().executeAsOne()
        )

        val taskService = ScoutifyClient.taskService
        val matchService: MatchService = ScoutifyClient.matchService
        val loginService: LoginService = CasdoorClient.loginService
        val userService: UserService = ScoutifyClient.userService
        val commentService: CommentService = ScoutifyClient.commentService
        val gameDetailsService: GameDetailsService = ScoutifyClient.gameDetailsService
        val teamNameService: TeamNameService = ScoutifyClient.teamNameService
        val appService: AppService = GithubClient.appService

        val userRepository = UserRepository(
            loginService = loginService,
            userService = userService,
            db = db,
            context = applicationContext
        )
        val taskRepository = TaskRepository(service = taskService, db = db)
        val matchRepository = MatchRepository(service = matchService, db = db)
        val gameDetailRepository = GameDetailRepository(
            gameDetailsService = gameDetailsService,
            appService = appService,
            db = db
        )
        val commentRepository = CommentRepository(service = commentService, db=db)
        val teamNameRepository = TeamNameRepository(service = teamNameService, db = db)

        val networkMonitor = NetworkMonitor(
            applicationContext,
            taskRepository = taskRepository,
            matchRepository = matchRepository,
            gameDetailRepository = gameDetailRepository,
            commentRepository = commentRepository,
            userRepository = userRepository,
            teamNameRepository = teamNameRepository
        )

        val pitScoutingRepository = PitScoutingRepository(
            db = db,
            surveyService = ScoutifyClient.surveyService,
            networkMonitor = networkMonitor,
            userRepository = userRepository,
            teamNameRepository = teamNameRepository,
            context = applicationContext
        )

        networkMonitor.repoList = networkMonitor.repoList + pitScoutingRepository

        UpdateManager.context = applicationContext

        ProcessLifecycleOwner.get().lifecycleScope.launch {

            if (NetworkMonitorStatus.currentNetworkJob != null) {
                try {
                    NetworkMonitorStatus.currentNetworkJob!!.cancel()
                } catch (e: Exception) {
                    Log.d("NetworkMonitor", "Job already cancelled: $e")
                }
            }

            NetworkMonitorStatus.currentNetworkJob = launch {
                networkMonitor.networkSync(maxErrors = 10)
            }

            if (
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }

            val receiver = UpdateReceiver(
                onFail = {
                    this.launch {
                        val githubResult: GithubResponse = appService.getAssets()
                        val link: Asset? = githubResult.assets.find {
                            it.browserDownloadUrl.takeLast(4) == "apk"
                        }

                        if (link != null) {
                            UpdateManager.downloadUpdate(link.browserDownloadUrl)
                        } else {
                            Log.d("Game Constants", "Update url not found!")
                        }
                    }
                },
                onSuccess = { installIntent ->
                    applicationContext.startActivity(installIntent)
                })

            applicationContext.registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                RECEIVER_EXPORTED
            )

            UpdateReceiver.deleteInstalledApk(applicationContext)
        }

        setContent {
            ScoutifyTheme {
                Root(
                    taskRepository = taskRepository,
                    matchRepository = matchRepository,
                    userRepository = userRepository,
                    gameDetailRepository = gameDetailRepository,
                    commentRepository = commentRepository,
                    teamNameRepository = teamNameRepository,
                    pitScoutingRepository = pitScoutingRepository,
                    networkMonitor = networkMonitor
                )
            }
        }
    }
}




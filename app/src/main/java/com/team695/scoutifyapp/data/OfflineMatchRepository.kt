package com.team695.scoutifyapp.data

/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import kotlinx.coroutines.flow.Flow

class OfflineMatchRepository(private val matchDAO: MatchDAO) : MatchRepository {
    override fun getAllItemsStream(): Flow<List<Match>> = matchDAO.getAllItems()

    override fun getItemStream(id: Int): Flow<Match?> = matchDAO.getItem(id)

    override suspend fun insertItem(item: Match) = matchDAO.insert(item)

    override suspend fun deleteItem(item: Match) = matchDAO.delete(item)

    override suspend fun updateItem(item: Match) = matchDAO.update(item)
}
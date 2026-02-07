package com.team695.scoutifyapp.data.objectBox.Entities

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import io.objectbox.relation.ToOne

@Entity
data class Action(
    @Id var id: Long = 0,
    @Convert(converter = ActionTypeConverter::class, dbType = Int::class)
    var type: ActionType = ActionType.Defense,
    var timestamp: Long = 0,
) {
    lateinit var match: ToOne<Match>
}
enum class ActionType {
    Defense,
    Cycle,
    Stockpile,
    Brick
}

class ActionTypeConverter : PropertyConverter<ActionType, Int> {
    override fun convertToDatabaseValue(entityProperty: ActionType): Int {
        return entityProperty.ordinal
    }

    override fun convertToEntityProperty(databaseValue: Int): ActionType {
        return ActionType.entries[databaseValue]
    }
}
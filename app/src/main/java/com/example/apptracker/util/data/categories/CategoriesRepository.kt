package com.example.apptracker.util.data.categories

class CategoriesRepository(
    private val dao: CategoriesDao
) {

    fun getCategories(
        showHidden: Boolean = false
    ): List<Category> {
        val items = dao.getAll()

        if (items.isEmpty()) {
            val newCategory = Category(
                id = 0,
                name = "Default",
                position = 0,
                hidden = true
            )
            addCategory(newCategory)
            return items + listOf(newCategory)
        }

        return items.dropWhile { it.hidden && !showHidden }.sortedBy { it.position }
    }

    fun addCategory(category: Category) {
        dao.insert(category)
    }

    fun setPosition(category: Category, newPosition: Int) {
        dao.setPosition(category.id, newPosition)
    }

    fun setName(category: Category, name: String) {
        dao.setName(category.id, name)
    }

    fun deleteCategory(category: Category) {
        val id = category.id
        dao.delete(id)
        // reformat categories
        val categories = dao.getAll().sortedBy { it.position }
        categories.forEachIndexed { index, it ->
            setPosition(it, index)
        }
    }

}
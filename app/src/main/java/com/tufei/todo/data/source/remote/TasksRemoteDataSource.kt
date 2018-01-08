package com.tufei.todo.data.source.remote

/**
 * @author tufei
 * @date 2018/1/7.
 */
class TasksRemoteDataSource private constructor() {
    companion object {

        private var INSTANCE: TasksRemoteDataSource? = null

        @JvmStatic
        fun getInstance(): TasksRemoteDataSource {
            return INSTANCE ?: TasksRemoteDataSource().apply { INSTANCE = this }
        }
    }
}
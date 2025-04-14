package com.aronid.weighttrackertft.data.workout

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class WorkoutPagingSource(
    private val query: Query,
    private val startDate: Timestamp? = null,
    private val endDate: Timestamp? = null
) : PagingSource<QuerySnapshot, WorkoutModel>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, WorkoutModel> {
        return try {
            Log.d("WorkoutPagingSource", "Loading page with size: ${params.loadSize}, startDate: $startDate, endDate: $endDate")
            var baseQuery = query.limit(params.loadSize.toLong())
            if (startDate != null) {
                baseQuery = baseQuery.whereGreaterThanOrEqualTo("date", startDate)
            }
            if (endDate != null) {
                baseQuery = baseQuery.whereLessThanOrEqualTo("date", endDate)
            }
            val currentPage = params.key ?: baseQuery.get().await()
            Log.d("WorkoutPagingSource", "Documents loaded: ${currentPage.size()}")

            val lastDocument = currentPage.documents.lastOrNull()
            val nextPage = lastDocument?.let {
                baseQuery.startAfter(it).get().await()
            }

            val workouts = currentPage.toObjects(WorkoutModel::class.java)
                .mapIndexed { index, workout ->
                    workout.copy(id = currentPage.documents[index].id)
                }
            Log.d("WorkoutPagingSource", "Workouts transformed: $workouts")

            LoadResult.Page(
                data = workouts,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            Log.e("WorkoutPagingSource", "Error loading page", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, WorkoutModel>): QuerySnapshot? = null
}
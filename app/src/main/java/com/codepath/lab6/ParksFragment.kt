
package com.codepath.lab6

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

private const val TAG = "ParksFragment"
private val PARKS_URL =
    "https://developer.nps.gov/api/v1/parks?api_key=${BuildConfig.API_KEY}"

class ParksFragment : Fragment(R.layout.fragment_parks) {

    private val parks = mutableListOf<Park>()
    private lateinit var parksAdapter: ParksAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val parksRecyclerView =
            view.findViewById<RecyclerView>(R.id.parks)

        val layoutManager = LinearLayoutManager(requireContext())

        parksRecyclerView.layoutManager = layoutManager
        parksAdapter = ParksAdapter(requireContext(), parks)
        parksRecyclerView.adapter = parksAdapter

        parksRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                layoutManager.orientation
            )
        )

        fetchParks()
    }

    private fun fetchParks() {
        val client = AsyncHttpClient()

        client.get(PARKS_URL, object : JsonHttpResponseHandler() {

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "Failed to fetch parks: $statusCode")
            }

            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JSON
            ) {
                try {
                    val parsedJson = createJson().decodeFromString(
                        ParksResponse.serializer(),
                        json.jsonObject.toString()
                    )

                    if (!isAdded || view == null) return

                    parsedJson.data?.let { list ->
                        parks.clear()
                        parks.addAll(list)
                        parksAdapter.notifyDataSetChanged()
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing parks", e)
                }
            }
        })
    }
}


package com.codepath.lab6

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class HomeFragment : Fragment() {

    private val featuredCampgrounds = mutableListOf<Campground>()
    private lateinit var adapter: CampgroundAdapter
    private lateinit var statusText: TextView

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 0)
        }


        val title = TextView(requireContext()).apply {
            text = "Explore National Park Campgrounds"
            textSize = 24f
            setPadding(0, 0, 0, 12)
            setTextColor(
                androidx.core.content.ContextCompat.getColor(
                    requireContext(),
                    R.color.camp_text_primary
                )
            )
        }



        val subtitle = TextView(requireContext()).apply {
            text = "Discover campgrounds from the National Park Service"
            textSize = 15f
            setPadding(0, 0, 0, 16)
            setTextColor(
                androidx.core.content.ContextCompat.getColor(
                    requireContext(),
                    R.color.camp_text_secondary
                )
            )
        }


        statusText = TextView(requireContext()).apply {
            setTextColor(
                androidx.core.content.ContextCompat.getColor(
                    requireContext(),
                    R.color.camp_text_secondary
                )
            )

            text = "Loading campgrounds..."
            textSize = 14f
        }

        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CampgroundAdapter(
                requireContext(),
                featuredCampgrounds
            ).also { this@HomeFragment.adapter = it }

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(statusText)
        layout.addView(recyclerView)

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchFeaturedCampgrounds()
    }

    private fun fetchFeaturedCampgrounds() {

        val url =
            "https://developer.nps.gov/api/v1/campgrounds" +
                    "?limit=12&api_key=${BuildConfig.API_KEY}"

        AsyncHttpClient().get(
            url,
            object : JsonHttpResponseHandler() {

                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    response: String?,
                    throwable: Throwable?
                ) {
                    Log.e("HomeFragment", "API failed: $statusCode")

                    if (isAdded && view != null) {
                        statusText.text = "Unable to load campgrounds."
                    }
                }

                override fun onSuccess(
                    statusCode: Int,
                    headers: Headers,
                    json: JSON
                ) {
                    try {
                        val response = createJson().decodeFromString(
                            CampgroundResponse.serializer(),
                            json.jsonObject.toString()
                        )

                        if (!isAdded || view == null) return

                        featuredCampgrounds.clear()
                        featuredCampgrounds.addAll(
                            response.data.orEmpty()
                        )

                        adapter.notifyDataSetChanged()

                        statusText.text =
                            if (featuredCampgrounds.isEmpty())
                                "No campgrounds found."
                            else
                                "Featured campgrounds (${featuredCampgrounds.size})"

                    } catch (e: Exception) {
                        Log.e("HomeFragment", "Parsing failed", e)

                        if (isAdded && view != null) {
                            statusText.text = "Unable to display campgrounds."
                        }
                    }
                }
            }
        )
    }
}

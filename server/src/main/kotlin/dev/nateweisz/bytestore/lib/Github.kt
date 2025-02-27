package dev.nateweisz.bytestore.lib

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

/**
 * We created this because the library we use for the github api makes too many requests for it to be viable listing commits.
 */
object Github {

    /**
     * Get the commits for a repository in a single request.
     */
    fun getRepositoryCommits(user: String, repo: String, accessToken: String? = null): JSONArray? {
        // I want to consider writing a full fledged api cause I hate how many requests the other one makes and handle ratelimiting with a list of keys
        val client = OkHttpClient()
        val url = "https://api.github.com/repos/$user/$repo/commits?per_page=10"

        val requestBuilder = Request.Builder()
            .url(url)
            .get()

        accessToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        val response = client.newCall(request).execute()


        if (!response.isSuccessful) {
            response.body.close()
            return null
        }

        try {
            val responseBody = response.body.string()
            return JSONArray(responseBody)
        } finally {
            response.body.close()
        }
    }

    fun isValidCommitHash(user: String, repo: String, commitHash: String, accessToken: String? = null): Boolean {
        val client = OkHttpClient()
        val url = "https://api.github.com/repos/$user/$repo/commits/$commitHash"

        val requestBuilder = Request.Builder()
            .url(url)
            .get()

        accessToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        val response = client.newCall(request).execute()
        response.body.close()

        return response.isSuccessful
    }
}
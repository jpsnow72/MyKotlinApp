package jcpsnowproductions.mykotlinapp

import android.os.AsyncTask
import java.net.URL

class NetworkRequests{

    fun URLRequest(urlString: String): String
    {
        val result = URL(urlString).readText()
        return result
    }

}
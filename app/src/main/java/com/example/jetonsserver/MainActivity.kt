package com.example.jetonsserver

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetonsserver.ui.theme.JetonsServerTheme
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections.list
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set


class MainActivity : ComponentActivity() {

    private var server: HttpServer? = null


    fun getLocalIpAddress(): String? {
        val interfaces = list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addrs = list(intf.inetAddresses)
            for (addr in addrs) {
                if (!addr.isLoopbackAddress && addr is InetAddress) {
                    val ip = addr.hostAddress
                    if (ip.indexOf(':') < 0) return ip // Skip IPv6
                }
            }
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start the server
        server = HttpServer(8080)
        try {
            server?.start()
            Toast.makeText(this, "Server started on port 8080", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to start server", Toast.LENGTH_SHORT).show()
        }

        setContent {
            val ipAddress = getLocalIpAddress()
            val port = 8080
            // val url = "http://${ipAddress}:${port}"


            JetonsServerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->

                    if (ipAddress != null) {
                        ServerInfoDisplay(ip = ipAddress, port = port)
                    } else {
                        Text("Unable to get local IP address")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        server?.stop()
    }
}

fun generateQrCode(text: String, size: Int = 512): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)

    val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap[x, y] =
                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
        }
    }
    return bitmap
}

@Composable
fun ServerInfoDisplay(ip: String, port: Int) {
    val url = "http://$ip:$port"
    val qrBitmap = remember(url) { generateQrCode(url) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Server running at:")
        Text(text = url)
        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier
                .size(200.dp)
                .padding(top = 8.dp)
        )
    }
}
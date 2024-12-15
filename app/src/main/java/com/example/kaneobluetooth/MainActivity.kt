package com.example.kaneobluetooth

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.example.kaneobluetooth.ui.theme.KaneOBluetoothTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val PERMISSION_BLUETOOTH = 1
        const val PERMISSION_BLUETOOTH_ADMIN = 2
        const val PERMISSION_BLUETOOTH_CONNECT = 3
        const val PERMISSION_BLUETOOTH_SCAN = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaneOBluetoothTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CarveryGridButtons(modifier: Modifier = Modifier) {
    val buttonSize = 100.dp

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CarveryButton("Adult Carvery", Color.Red, buttonSize)
            CarveryButton("Kids Carvery", Color.Blue, buttonSize)
            CarveryButton("Vegan Carvery", Color.Magenta, buttonSize)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CarveryButton("O.A.P Carvery", Color.Yellow, buttonSize)
            CarveryButton("Extra Pigs", Color(0xFF800080), buttonSize) // Custom Purple
            CarveryButton("Extra Yorkie", Color.Cyan, buttonSize)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CarveryButton("Baby Bowl", Color.Green, buttonSize)
        }
    }
}

@Composable
fun CarveryButton(label: String, color: Color, size: androidx.compose.ui.unit.Dp) {
    Button(
        onClick = { /* Handle click */ },
        modifier = Modifier
            .size(size)
            .background(Color.White),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.Black)
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        permissionGranted = checkAndRequestBluetoothPermissions(context)
    }

    if (permissionGranted) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Bluetooth Permissions Granted")
            Spacer(modifier = Modifier.height(16.dp))
            CarveryGridButtons(modifier = Modifier.fillMaxWidth()) // Add grid buttons
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { performPrint(context) }) {
                Text("Print")
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Please grant Bluetooth permissions")
        }
    }
}

fun checkAndRequestBluetoothPermissions(context: android.content.Context): Boolean {
    val activity = context as? ComponentActivity ?: return false

    return when {
        android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED -> {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH), MainActivity.PERMISSION_BLUETOOTH)
            false
        }
        android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED -> {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_ADMIN), MainActivity.PERMISSION_BLUETOOTH_ADMIN)
            false
        }
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED -> {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), MainActivity.PERMISSION_BLUETOOTH_CONNECT)
            false
        }
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED -> {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_SCAN), MainActivity.PERMISSION_BLUETOOTH_SCAN)
            false
        }
        else -> true
    }
}

fun performPrint(context: android.content.Context) {
    try {
        val printer = EscPosPrinter(
            BluetoothPrintersConnections.selectFirstPaired(),
            203,
            80f,
            32
        )

        printer.printFormattedTextAndCut(
            "[C]<img>" +
                    PrinterTextParserImg.bitmapToHexadecimalString(
                        printer,
                        context.resources.getDrawableForDensity(
                            R.drawable.logo,
                            DisplayMetrics.DENSITY_MEDIUM
                        )
                    ) + "</img>\n" +
                    "[L]\n" +
                    "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                    "[L]\n" +
                    "[C]================================\n" +
                    "[L]\n" +
                    "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
                    "[L]  + Size : S\n" +
                    "[L]\n" +
                    "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
                    "[L]  + Size : 57/58\n" +
                    "[L]\n" +
                    "[C]--------------------------------\n" +
                    "[R]TOTAL PRICE :[R]34.98e\n" +
                    "[R]TAX :[R]4.23e\n" +
                    "[L]\n" +
                    "[C]================================\n" +
                    "[L]\n" +
                    "[L]<font size='tall'>Customer :</font>\n" +
                    "[L]Raymond DUPONT\n" +
                    "[L]5 rue des girafes\n" +
                    "[L]31547 PERPETES\n" +
                    "[L]Tel : +33801201456\n" +
                    "[L]\n" +
                    "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
                    "[C]<qrcode size='20'>https://dantsu.com/</qrcode>"
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

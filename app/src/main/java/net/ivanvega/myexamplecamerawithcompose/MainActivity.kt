package net.ivanvega.myexamplecamerawithcompose

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import net.ivanvega.myexamplecamerawithcompose.ui.theme.MyExampleCameraWithComposeTheme
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    //El photoUri se almacenará en la memoria para que pueda usarse para mostrar la imagen. shouldShowPhotofunciona como shouldShowCameradonde
    // será el Booleanresponsable de mostrar la foto una vez capturada.
    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    // El bloque de esta función pasará un Booleanque especifica si se otorgó el permiso de solicitud.
    //permiso de la camata, aqui se lo otorgamos o no
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("kilo", "Permission granted")
            shouldShowCamera.value = true
        } else {
            Log.i("kilo", "Permission denied")
        }
    }

    // Cree una función que solicite el permiso de la cámara.
    //La declaración when  determinará a qué paso se debe dirigir al usuario al verificar el permiso de la cámara del manifiesto.
    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
                shouldShowCamera.value = true // 👈🏽
                //Ya sea que el permiso de la cámara se haya otorgado en el aviso inicial del sistema operativo o se haya otorgado anteriormente,
                // se shouldShowCamera.valueactualiza para que la vista previa de la cámara se muestre en la pantalla.
                // El manejo del permiso denegado también debe manejarse en consecuencia.
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("kilo", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    //Asegúrese de llamar a la función.
    //Cree y ejecute para ver el aviso del sistema operativo para permitir los permisos de la cámara.
    // Dependiendo de la opción seleccionada, verá las diferentes declaraciones registradas en la consola.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (shouldShowCamera.value) {

                //Aqui es cuando nos pide el permiso la aplicacion, nos muestra una pantalla de camara pero encecima nos muestra la
                //peticion de permiso.

                //Solo se CameraView mostrará si shouldShowCameratiene un valor de true. De forma predeterminada,
                // es falso y debe actualizarse cuando se otorga el permiso de la cámara.
                CameraView(
                    outputDirectory = outputDirectory,
                    executor = cameraExecutor,
                    onImageCaptured = ::handleImageCapture,
                    onError = { Log.e("kilo", "View error:", it) }
                )
            }
            //La imagen ahora se mostrará cuando shouldShowPhotose haya actualizado atrue
            if (shouldShowPhoto.value) {
                Image(
                    painter = rememberImagePainter(photoUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        requestCameraPermission()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }


    //Aqui se conserva la imagen capturada
    //El enfoque aquí es mantener el directorio de salida, el ejecutor de la cámara y debería mostrar el
    // booleano de la cámara en la memoria. Las funciones ayudan a administrar esas propiedades.
    private fun handleImageCapture(uri: Uri) {
        Log.i("kilo", "Image captured: $uri")
        photoUri = uri
        shouldShowPhoto.value = true
        shouldShowCamera.value = false
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
//**********************************
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyExampleCameraWithComposeTheme {
        Greeting("Android")
    }
}
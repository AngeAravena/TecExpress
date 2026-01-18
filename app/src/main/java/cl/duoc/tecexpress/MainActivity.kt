package cl.duoc.tecexpress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cl.duoc.tecexpress.navigation.NavGraph
import cl.duoc.tecexpress.ui.service.ServiceScreen
import cl.duoc.tecexpress.ui.theme.TecExpressTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as TecExpressApplication
        setContent {
            TecExpressTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NavGraph(app = app)
                }
            }
        }
    }
}

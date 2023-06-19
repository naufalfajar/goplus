package id.naufalfajar.go.view.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import id.naufalfajar.go.MainActivity
import id.naufalfajar.go.R
import id.naufalfajar.go.helper.DataStoreManager
import kotlinx.coroutines.launch

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var preferenceManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        preferenceManager = DataStoreManager(this)
        lifecycleScope.launch {
            preferenceManager.isFirstTimeUser.collect { isFirstTime ->
                if (!isFirstTime) {
                    // Lakukan sesuatu untuk pengguna baru
                    // Navigasikan ke halaman onboarding
                    val intent = Intent (this@OnBoardingActivity, MainActivity::class.java)
                    startActivity(intent)
                    this@OnBoardingActivity.finish()
                } else {
                    setContentView(R.layout.activity_on_boarding)
                }
            }
        }
        super.onCreate(savedInstanceState)
    }
}
package id.naufalfajar.go.view.detection

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.naufalfajar.go.databinding.ActivityDetectionBinding

class DetectionActivity : AppCompatActivity() {
    private var _binding: ActivityDetectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
            // (https://issuetracker.google.com/issues/139738913)
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }
}
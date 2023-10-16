package id.naufalfajar.go.view

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.naufalfajar.go.adapter.ReminderAdapter
import id.naufalfajar.go.databinding.FragmentScheduleAddBinding
import id.naufalfajar.go.helper.channelID
import id.naufalfajar.go.helper.messageExtra
import id.naufalfajar.go.helper.notificationID
import id.naufalfajar.go.helper.titleExtra
import id.naufalfajar.go.helper.Notification
import id.naufalfajar.go.model.Place
import id.naufalfajar.go.model.Schedule
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleAddFragment : Fragment() {
    private var _binding: FragmentScheduleAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var placeList: Array<String>
    private var db = Firebase.firestore
    private val reminderList = mutableListOf<Schedule>()
    private lateinit var reminderAdapter: ReminderAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleAddBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        onBack()
        datePicker()
        timePicker()
        createNotificationChannel()
        saveSchedule()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun datePicker(){
        binding.etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(cal.time)
                binding.etDate.setText(formattedDate)
            }

            DatePickerDialog(requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun timePicker(){
        binding.etTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                binding.etTime.setText(SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time))
            }

            TimePickerDialog(requireContext(), timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), true).show()
        }
    }

    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = requireActivity().applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    private fun scheduleNotification() {
        try {
            Log.d("Schedule", "Scheduling notification...")
            val intent = Intent(requireActivity().applicationContext, Notification::class.java)
            intent.action = "id.naufalfajar.go.NOTIFY"  // Tambahkan baris ini
            val title = "Anda mempunyai jadwal perjalanan"
            val message = "Perjalanan ke ${binding.etPlace.text}"
            intent.putExtra(titleExtra, title)
            intent.putExtra(messageExtra, message)

            val pendingIntent = PendingIntent.getBroadcast(
                requireActivity().applicationContext,
                notificationID,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val time = getDateTimeInMillis()
            val alarmManager = requireActivity().applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Tampilkan pesan error atau informasi ke pengguna
            Toast.makeText(requireContext(), "Izin diperlukan untuk mengatur pengingat", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTimeInMillisFromDateTime(date: String, time: String): Long {
        val dateTimeString = "$date $time"
        val dateTimeFormat = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", Locale.getDefault())
        val dateTime = dateTimeFormat.parse(dateTimeString)
        return dateTime?.time ?: 0
    }

    // Menggunakan fungsi getTimeInMillisFromDateTime
    private fun getDateTimeInMillis(): Long {
        val date = binding.etDate.text.toString()
        val time = binding.etTime.text.toString()

        return getTimeInMillisFromDateTime(date, time)
    }

    private fun saveSchedule(){
        binding.mbtnSave.setOnClickListener {
            if(validateEditTexts()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = requireActivity().applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        intent.data = Uri.parse("package:${requireContext().packageName}")
                        startActivity(intent)
                    } else {
                        scheduleNotification()
                    }
                } else {
                    scheduleNotification()
                }
                Toast.makeText(requireContext(), "Berhasil Set Pengingat", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun validateEditTexts(): Boolean {
        val etPlace = binding.etPlace
        val etDate = binding.etDate
        val etTime = binding.etTime

        var isValid = true

        if (etPlace.text.toString().isEmpty()) {
            etPlace.error = "Tempat harus diisi"
            isValid = false
        }

        if (etDate.text.toString().isEmpty()) {
            etDate.error = "Tanggal harus diisi"
            isValid = false
        }

        if (etTime.text.toString().isEmpty()) {
            etTime.error = "Waktu harus diisi"
            isValid = false
        }

        return isValid
    }

    private fun onBack(){
        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun getData(){
        placeList = arrayOf()
        db = FirebaseFirestore.getInstance()
        db.collection("place").get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for(data in it.documents){
                        val dataPlace = data.toObject(Place::class.java)
                        if(dataPlace != null){
                            placeList.plus(dataPlace.name!!)
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
            }
    }
}
package com.example.zapis_version3.ui.dashboard

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.zapis_version3.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = FirebaseDatabase.getInstance().reference

        val chooseDateEditText = binding.choiceData
        chooseDateEditText.isFocusable = false
        chooseDateEditText.isClickable = true
        chooseDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        val chooseTimeEditText = binding.choiceTime
        chooseTimeEditText.isFocusable = false
        chooseTimeEditText.isClickable = true
        chooseTimeEditText.setOnClickListener {
            showTimePickerDialog()
        }

        val mestoOptions = arrayOf("МФЦ", "Сбербанк", "Нотариус")
        val chooseMestoSpinner = binding.choiceMesto
        val mestoAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mestoOptions)
        mestoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        chooseMestoSpinner.adapter = mestoAdapter

        setUslugaOptionsForMesto1()
        setAdresOptionsForMesto1()

        val choiceMesto: Spinner = binding.choiceMesto
        choiceMesto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        setUslugaOptionsForMesto1()
                        setAdresOptionsForMesto1()
                    }
                    1 -> {
                        setUslugaOptionsForMesto2()
                        setAdresOptionsForMesto2()
                    }
                    2 -> {
                        setUslugaOptionsForMesto3()
                        setAdresOptionsForMesto3()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val continueButton = binding.buttonAuth
        continueButton.setOnClickListener {
            if (validateFields()) {
                val chooseMesto = binding.choiceMesto
                val place = chooseMesto.selectedItem.toString()
                val chooseUsluga = binding.choiceUsluga
                val uslug = chooseUsluga.selectedItem.toString()
                val chooseAdres = binding.choiceAdres
                val adres = chooseAdres.selectedItem.toString()
                val chooseDateEditText = binding.choiceData
                val date = chooseDateEditText.text.toString()
                val chooseTimeEditText = binding.choiceTime
                val time = chooseTimeEditText.text.toString()

                saveDataToFirebase(
                    FirebaseAuth.getInstance().currentUser?.email ?: "",
                    place,
                    uslug,
                    adres,
                    "$date $time"
                )
                clearFields()
                showSuccessDialog(place)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clearFields() {
        binding.choiceMesto.setSelection(0)
        binding.choiceUsluga.setSelection(0)
        binding.choiceAdres.setSelection(0)
        binding.choiceData.text.clear()
        binding.choiceTime.text.clear()
    }

    private fun showSuccessDialog(place: String) {
        val message = "Вы успешно записались в $place! Данные о записи сохранены в Мой профиль -> Мои записи."

        val spannableMessage = SpannableString(message)
        val startIndex = message.indexOf(place)
        val endIndex = startIndex + place.length
        val boldStyle = StyleSpan(android.graphics.Typeface.BOLD)

        spannableMessage.setSpan(boldStyle, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Успешная запись")
        builder.setMessage(spannableMessage)
        builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = "$dayOfMonth-${monthOfYear + 1}-$year"
                val chooseDateEditText = binding.choiceData
                chooseDateEditText.setText(selectedDate)
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            val chooseTimeEditText = binding.choiceTime
            chooseTimeEditText.setText(selectedTime)
        }

        val timePickerDialog = TimePickerDialog(requireContext(), AlertDialog.THEME_HOLO_LIGHT, timeSetListener, hour, minute, true)
        timePickerDialog.show()
    }

    private fun setUslugaOptionsForMesto1() {
        val choiceUsluga: Spinner = binding.choiceUsluga
        val uslugaList = listOf("Получить загранпаспорт", "Получить выписку из ЕГРН", "Получить паспорт")
        val uslugaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, uslugaList)
        uslugaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choiceUsluga.adapter = uslugaAdapter
    }

    private fun setUslugaOptionsForMesto2() {
        val choiceUsluga: Spinner = binding.choiceUsluga
        val uslugaList = listOf("Получить карту", "Открыть вклад", "Оставить заявку на кредит")
        val uslugaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, uslugaList)
        uslugaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choiceUsluga.adapter = uslugaAdapter
    }

    private fun setUslugaOptionsForMesto3() {
        val choiceUsluga: Spinner = binding.choiceUsluga
        val uslugaList = listOf("Заверить документы")
        val uslugaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, uslugaList)
        uslugaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choiceUsluga.adapter = uslugaAdapter
    }

    private fun setAdresOptionsForMesto1() {
        val choiceAdres: Spinner = binding.choiceAdres
        val adresList = listOf("ул. Свердловская, 69 ", "ул. Кирова, 43", "ул. Телевизорная, 1")
        val adresAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, adresList)
        adresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choiceAdres.adapter = adresAdapter
    }

    private fun setAdresOptionsForMesto2() {
        val choiceAdres: Spinner = binding.choiceAdres
        val adresList = listOf("ул. Ленина, 126", "ул. Сурикова, 12/6", "ул. Высотная, 27")
        val adresAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, adresList)
        adresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choiceAdres.adapter = adresAdapter
    }

    private fun setAdresOptionsForMesto3() {
        val choiceAdres: Spinner = binding.choiceAdres
        val adresList = listOf("ул. Копылова, 17", "ул. Батурина, 5", "ул. Весны, 17")
        val adresAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, adresList)
        adresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choiceAdres.adapter = adresAdapter
    }

    private fun validateFields(): Boolean {
        val choiceMesto: Spinner = binding.choiceMesto
        val mesto = choiceMesto.selectedItem.toString()
        val choiceUsluga: Spinner = binding.choiceUsluga
        val usluga = choiceUsluga.selectedItem.toString()
        val choiceAdres: Spinner = binding.choiceAdres
        val adres = choiceAdres.selectedItem.toString()

        return mesto.isNotEmpty() && usluga.isNotEmpty() && adres.isNotEmpty()
    }

    private fun saveDataToSharedPreferences(context: Context, place: String, uslug: String, adres: String, dateTime: String) {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("place", place)
        editor.putString("uslug", uslug)
        editor.putString("adres", adres)
        editor.putString("dateTime", dateTime)
        editor.apply()
    }

    private fun saveDataToFirebase(userEmail: String, place: String, uslug: String, adres: String, dateTime: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        if (userId != null) {
            val dataId = UUID.randomUUID().toString()
            val data = HashMap<String, String>()
            data["userId"] = userId
            data["userEmail"] = userEmail
            data["place"] = place
            data["uslug"] = uslug
            data["adres"] = adres
            data["dateTime"] = dateTime

            Log.d("FirebaseSave", "Data to be saved: $data")

            database.child("Data").child(userId).child(dataId).setValue(data)
                .addOnSuccessListener {
                    Log.d("FirebaseSave", "Data saved successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseSave", "Error saving data: $e")
                }
        }
    }

    private fun getDataFromSharedPreferences(context: Context): Triple<String, String, String> {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val place = sharedPreferences.getString("place", "") ?: ""
        val uslug = sharedPreferences.getString("uslug", "") ?: ""
        val adres = sharedPreferences.getString("adres", "") ?: ""
        val dateTime = sharedPreferences.getString("dateTime", "") ?: ""
        return Triple(place, uslug, "$adres $dateTime")
    }
}

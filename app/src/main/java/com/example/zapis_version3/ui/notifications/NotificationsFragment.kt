package com.example.zapis_version3.ui.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.zapis_version3.R

class NotificationsFragment : Fragment() {

    private lateinit var choiceMesto: Spinner
    private lateinit var choiceUsluga: Spinner
    private lateinit var choiceAdres: Spinner
    private lateinit var buttonAuth: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        val mestoOptions = arrayOf("МФЦ", "Сбербанк", "Нотариус")
        choiceMesto = view.findViewById(R.id.choice_mesto)
        val mestoAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mestoOptions)
        mestoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choiceMesto.adapter = mestoAdapter

        choiceUsluga = view.findViewById(R.id.choice_usluga)
        choiceAdres = view.findViewById(R.id.choice_adres)

        choiceMesto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> setMFCOptions()
                    1 -> setSberbankOptions()
                    2 -> setNotariusOptions()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        buttonAuth = view.findViewById(R.id.button_auth)
        buttonAuth.setOnClickListener {
            val selectedMesto = choiceMesto.selectedItem.toString()
            val selectedUsluga = choiceUsluga.selectedItem.toString()
            val selectedAdres = choiceAdres.selectedItem.toString()

            saveDataToSharedPreferences(requireContext(), selectedMesto, selectedUsluga, selectedAdres)

            val intent = Intent(requireActivity(), VstalVocheredActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun setMFCOptions() {
        val uslugaOptions = arrayOf("Получить загранпаспорт", "Получить выписку из ЕГРН", "Получить паспорт")
        val adresOptions = arrayOf("ул. Свердловская, 69 ", "ул. Кирова, 43", "ул. Телевизорная, 1")

        val uslugaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, uslugaOptions)
        uslugaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choiceUsluga.adapter = uslugaAdapter

        val adresAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, adresOptions)
        adresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choiceAdres.adapter = adresAdapter
    }

    private fun setSberbankOptions() {
        val uslugaOptions = arrayOf("Получить карту", "Открыть вклад", "Оставить заявку на кредит")
        val adresOptions = arrayOf("ул. Ленина, 126", "ул. Сурикова, 12/6", "ул. Высотная, 27")

        val uslugaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, uslugaOptions)
        uslugaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choiceUsluga.adapter = uslugaAdapter

        val adresAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, adresOptions)
        adresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choiceAdres.adapter = adresAdapter
    }

    private fun setNotariusOptions() {
        val uslugaOptions = arrayOf("Заверить документы")
        val adresOptions = arrayOf("ул. Копылова, 17", "ул. Батурина, 5", "ул. Весны, 17")

        val uslugaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, uslugaOptions)
        uslugaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choiceUsluga.adapter = uslugaAdapter

        val adresAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, adresOptions)
        adresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choiceAdres.adapter = adresAdapter
    }

    private fun saveDataToSharedPreferences(context: Context, place: String, uslug: String, adres: String) {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("place", place)
        editor.putString("uslug", uslug)
        editor.putString("adres", adres)
        editor.apply()
    }

}

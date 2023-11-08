package com.example.zapis_version3.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.zapis_version3.R
import com.example.zapis_version3.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.android.material.bottomsheet.BottomSheetDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = FirebaseDatabase.getInstance().reference

        fetchUserDetailsFromFirebase()

        val myRecordsButton = root.findViewById<Button>(R.id.myRecordsButton)
        myRecordsButton.setOnClickListener {
            fetchUserRecordsFromFirebase()
        }

        return root
    }

    private fun fetchUserDetailsFromFirebase() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        if (userId != null) {
            database.child("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.value as Map<*, *>
                    val name = userData["name"].toString()
                    val surname = userData["surname"].toString()
                    val fullName = "$name $surname"
                    val email = userData["email"].toString()
                    val nameTextView = binding.imyaTv
                    val emailTextView = binding.textView2
                    nameTextView.text = fullName
                    emailTextView.text = email
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseFetch", "Error fetching user data: $error")
                }
            })
        }
    }

    private fun fetchUserRecordsFromFirebase() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        if (userId != null) {
            database.child("Data").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userRecords: MutableList<String> = mutableListOf()
                    for (recordSnapshot in snapshot.children) {
                        val recordData = recordSnapshot.value as Map<*, *>
                        val place = recordData["place"].toString()
                        val uslug = recordData["uslug"].toString()
                        val adres = recordData["adres"].toString()
                        val dateTime = recordData["dateTime"].toString()
                        val recordString = "$place, $uslug, $adres, $dateTime"
                        userRecords.add(recordString)
                    }
                    displayUserRecords(userRecords)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseFetch", "Error fetching data: $error")
                }
            })
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun displayUserRecords(records: List<String>) {
        if (isAdded) {
            val dialog = BottomSheetDialog(requireContext())
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
            val recordsLayout: LinearLayout = bottomSheetView.findViewById(R.id.recordsLayout)
            recordsLayout.removeAllViews()

            val inflater = LayoutInflater.from(requireContext())
            for (record in records) {
                val recordView = inflater.inflate(R.layout.record_item, recordsLayout, false) as LinearLayout
                val placeTextView = recordView.findViewById<TextView>(R.id.placeTextView)
                val uslugTextView = recordView.findViewById<TextView>(R.id.uslugTextView)
                val adresTextView = recordView.findViewById<TextView>(R.id.adresTextView)
                val dateTimeTextView = recordView.findViewById<TextView>(R.id.dateTimeTextView)
                val cancelButton = recordView.findViewById<Button>(R.id.cancelButton)

                val parts = record.split(", ")
                if (parts.size >= 5) {
                    placeTextView.text = "Место: ${parts[0]}"
                    uslugTextView.text = "Услуга: ${parts[1]}"
                    adresTextView.text = "Адрес: ${parts[2]} ${parts[3]}"
                    dateTimeTextView.text = "Дата и время: ${parts[4]}"
                }

                cancelButton.setOnClickListener {
                    if (parts.size >= 5) {
                        val place = parts[0]
                        val uslug = parts[1]
                        val adres = parts[2] + " " + parts[3]
                        val dateTime = parts[4]
                        removeRecordFromFirebase(place, uslug, adres, dateTime)
                        recordsLayout.removeView(recordView)
                    }
                }
                recordsLayout.addView(recordView)
            }
            dialog.setContentView(bottomSheetView)
            dialog.show()
        }
    }


    private fun removeRecordFromFirebase(place: String, uslug: String, adres: String, dateTime: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        if (userId != null) {
            val query = database.child("Data").child(userId).orderByChild("dateTime").equalTo(dateTime)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (recordSnapshot in snapshot.children) {
                        val recordData = recordSnapshot.value as Map<*, *>
                        Log.d("FirebaseDelete", "Current place: ${recordData["place"]}")
                        Log.d("FirebaseDelete", "Current uslug: ${recordData["uslug"]}")
                        Log.d("FirebaseDelete", "Current adres: ${recordData["adres"]}")
                        Log.d("FirebaseDelete", "Current dateTime: ${recordData["dateTime"]}")

                        if (recordData["place"].toString() == place && recordData["uslug"].toString() == uslug
                            && recordData["adres"].toString() == adres
                        ) {
                            recordSnapshot.ref.removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    fetchUserRecordsFromFirebase()
                                } else {
                                    Log.e("FirebaseDelete", "Error deleting data: ${task.exception}")
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseDelete", "Error deleting data: $error")
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

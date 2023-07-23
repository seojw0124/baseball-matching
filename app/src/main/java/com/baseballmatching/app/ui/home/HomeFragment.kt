package com.baseballmatching.app.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.baseballmatching.app.R
import com.baseballmatching.app.databinding.FragmentHomeBinding
import com.baseballmatching.app.datamodel.Game
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        val homeAdapter = HomeAdapter { game ->
            // 클릭했을 때
            Log.d("HomeFragment", "onViewCreated: ${game.id}")
        }

        // firebase realtime database에서 데이터 가져오기
        Firebase.database.reference.child("games")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    /*val list = snapshot.children.map {
                        it.getValue(Game::class.java)
                    }
                    Log.d("HomeFragment", "onDataChange: ${list[0]}")

                    homeAdapter.submitList(list)*/

                    val gameList = mutableListOf<Game>()
                    snapshot.children.forEach {
                        val game = it.getValue(Game::class.java)
                        game ?: return

                        // 2023-08-01T18:30:00Z => 2023-08-01 18:30:00
                        val dateTime = game.time
                        val dateTimeSplit = dateTime?.split("T")
                        val date = dateTimeSplit?.get(0)
                        val time = dateTimeSplit?.get(1)?.split("Z")?.get(0)

                        val currentDate = LocalDate.now()

                        if (date == "2023-07-25") {
                            gameList.add(game)
                        }
                    }

                    homeAdapter.submitList(gameList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("HomeFragment", "onCancelled: ${error.message}")
                }
            })

        binding.rvGameListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
        }
    }
}
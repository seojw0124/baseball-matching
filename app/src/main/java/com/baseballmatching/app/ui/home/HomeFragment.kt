package com.baseballmatching.app.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.baseballmatching.app.utils.AssetLoader
import com.baseballmatching.app.R
import com.baseballmatching.app.databinding.FragmentHomeBinding
import com.baseballmatching.app.datamodel.Game
import com.baseballmatching.app.datamodel.GameDetail
import com.baseballmatching.app.datamodel.TeamData
import com.baseballmatching.app.ui.GameDetailActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.time.LocalDate

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeAdapter: HomeAdapter

    private lateinit var teamTitle: String
    @RequiresApi(Build.VERSION_CODES.O)
    private val currentDate = LocalDate.now().toString()
    private var gameList: MutableList<GameDetail> = mutableListOf()
    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDate = currentDate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        binding.tvDate.text = "${currentDate.substring(5, 7)}월 ${currentDate.substring(8, 10)}일"

        gameList.clear()

        homeAdapter = HomeAdapter { game ->
            // 클릭했을 때
            val intent = Intent(context, GameDetailActivity::class.java)
            intent.putExtra("game", game)

            startActivity(intent)
        }

        getGameList(currentDate)

        binding.rvGameListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
        }

        binding.ivRightArrow.setOnClickListener {
            // currentDate에 1일 더하기
            gameList = mutableListOf()
            val dateSplit = selectedDate.split("-")
            val year = dateSplit[0].toInt()
            val month = dateSplit[1].toInt()
            val day = dateSplit[2].toInt()
            val nextDate = LocalDate.of(year, month, day).plusDays(1).toString()
            selectedDate = nextDate
            binding.tvDate.text = "${nextDate.substring(5, 7)}월 ${nextDate.substring(8, 10)}일"

            getGameList(nextDate)
        }
        binding.ivLeftArrow.setOnClickListener {
            // currentDate에 1일 빼기
            gameList = mutableListOf()
            val dateSplit = selectedDate.split("-")
            val year = dateSplit[0].toInt()
            val month = dateSplit[1].toInt()
            val day = dateSplit[2].toInt()
            val prevDate = LocalDate.of(year, month, day).minusDays(1).toString()
            selectedDate = prevDate
            binding.tvDate.text = "${prevDate.substring(5, 7)}월 ${prevDate.substring(8, 10)}일"

            getGameList(prevDate)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("HomeFragment", "onResume: ")

        //homeAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        gameList = mutableListOf()
    }

    private fun getGameList(dateString: String) {
        val assetLoader = AssetLoader()
        val teamsJsonString = assetLoader.getJsonString(requireContext(), "teams.json")
        Firebase.database.reference.child("games")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    /*val list = snapshot.children.map {
                        it.getValue(Game::class.java)
                    }
                    Log.d("HomeFragment", "onDataChange: ${list[0]}")
                    homeAdapter.submitList(list)*/
                    //val gameList = mutableListOf<GameDetail>()
                    snapshot.children.forEach {
                        val game = it.getValue(Game::class.java)
                        game ?: return

                        var homeLogo = ""
                        var awayLogo = ""
                        var ballpark = ""
                        var ballparkImage = ""

                        val gson = Gson()
                        val teamData = gson.fromJson(teamsJsonString, TeamData::class.java)
                        for (team in teamData.teams) {
                            if (team.teamName == game.home) {
                                homeLogo = team.logo.toString()
                                ballpark = team.ballpark.toString()
                                ballparkImage = team.ballparkImage.toString()
                            } else if (team.teamName == game.away) {
                                awayLogo = team.logo.toString()
                            }
                        }

                        // 2023-08-01T18:30:00Z => 2023-08-01 18:30:00
                        val dateTime = game.time
                        val dateTimeSplit = dateTime?.split("T")
                        val date = dateTimeSplit?.get(0)
                        val time = dateTimeSplit?.get(1)?.split("Z")?.get(0)

                        val gameDetail = GameDetail(game, homeLogo, awayLogo, ballpark, time, ballparkImage)

                        if (date == dateString) {
                            gameList.add(gameDetail)
                            Log.d("HomeFragment", "gameList: $gameList")
                        }
                    }

                    homeAdapter.submitList(gameList)

                    if (gameList.isEmpty()) {
                        binding.tvNoGame.visibility = View.VISIBLE
                        Log.d("HomeFragment", "onViewCreated: gameList is empty")
                    } else {
                        binding.tvNoGame.visibility = View.GONE
                        Log.d("HomeFragment", "onViewCreated: gameList is not empty")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HomeFragment", "onCancelled: ${error.toException()}")
                }
            })
    }
}
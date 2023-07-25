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
    //private var gameDetail: GameDetail? = null

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

        gameList.clear()

        homeAdapter = HomeAdapter { game ->
            // 클릭했을 때
            val intent = Intent(context, GameDetailActivity::class.java)
            intent.putExtra("game", game)
            Log.d("HomeFragment", "game: $game")

            startActivity(intent)
        }

        val assetLoader = AssetLoader()
        val teamsJsonString = assetLoader.getJsonString(requireContext(), "teams.json")

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

                        if (date == currentDate) {
                            gameList.add(gameDetail)
                            Log.d("HomeFragment", "gameList: $gameList")
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

    override fun onResume() {
        super.onResume()
        homeAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        gameList = mutableListOf()
    }


}
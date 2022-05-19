package koas.boos.egae.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import koas.boos.egae.R
import koas.boos.egae.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private var points = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonShuffle.setOnClickListener {
            shuffleItems()
            if (binding.imageView1.tag == binding.imageView2.tag
                && binding.imageView2.tag == binding.imageView3.tag
            ) {
                points++
                binding.textViewTotal.text = points.toString()
            }
        }
    }

    private fun shuffleItems() {
        val listImages = listOf(
            R.drawable.e1,
            R.drawable.e1,
            R.drawable.e1,
            R.drawable.e1,
            R.drawable.e1,
            R.drawable.e2,
            R.drawable.e2,
            R.drawable.e2,
            R.drawable.e2,
            R.drawable.e2,
            R.drawable.e3,
            R.drawable.e3,
            R.drawable.e3,
            R.drawable.e3,
            R.drawable.e3,
            R.drawable.e4,
            R.drawable.e4,
            R.drawable.e4,
            R.drawable.e4,
            R.drawable.e4,
        ).shuffled()
        binding.imageView1.setImageResource(listImages[0])
        binding.imageView1.tag = listImages[0]
        binding.imageView2.setImageResource(listImages[1])
        binding.imageView2.tag = listImages[1]
        binding.imageView3.setImageResource(listImages[2])
        binding.imageView3.tag = listImages[2]
    }
}
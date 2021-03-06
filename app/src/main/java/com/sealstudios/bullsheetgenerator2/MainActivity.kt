package com.sealstudios.bullsheetgenerator2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.sealstudios.bullsheetgenerator2.databinding.ActivityMainBinding
import com.sealstudios.bullsheetgenerator2.objects.SearchCriteria
import com.sealstudios.bullsheetgenerator2.utils.Constants
import com.sealstudios.bullsheetgenerator2.viewModels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        setUpSeekbar()
        setUpButtons()
        observeSearchCriteria()
        observeOnValidationFailed()
    }

    private fun setUpButtons() {
        binding.contentMain.getLocationButton.setOnClickListener {
            if (checkPermission(this)){
                setLastKnownLocation()
            } else {
                requestPermission(this)
            }
        }
        binding.contentMain.dateTo.setOnClickListener {  }
        binding.contentMain.dateFrom.setOnClickListener {  }
        binding.contentMain.submitBtn.setOnClickListener {
            searchViewModel.setJobTitle(binding.contentMain.jobDescriptionTextView.text.toString())
            if (searchViewModel.validate()){
                binding.contentMain.submitBtn.startAnimation()
            }
        }
    }

    private fun setToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setIcon(R.mipmap.toolbar_icon)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun observeSearchCriteria(){
        searchViewModel.searchCriteria.observe(this, {
            populateViews(it)
        })
    }

    private fun observeOnValidationFailed() {
        searchViewModel.onValidationFailed.observe(this, {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun setUpSeekbar(){
        binding.contentMain.seekbar.progress = 1
        binding.contentMain.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                searchViewModel.setRadius(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun populateViews(searchCriteria: SearchCriteria){
        binding.contentMain.jobLocationTextView.setText(searchCriteria.title)
        binding.contentMain.radiusText.text = searchCriteria.radius.toString()
        binding.contentMain.dateFrom.text = searchCriteria.dateFrom
        binding.contentMain.dateTo.text = searchCriteria.dateTo
        binding.contentMain.jobLocationTextView.setText(searchCriteria.postCode)
    }

    private fun checkPermission(context: Context): Boolean {
        val result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            Constants.PERMISSION_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLastKnownLocation()
                } else {
                    Snackbar.make(binding.root, "Enter a postcode", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestPermission(context: Context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(context, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Constants.PERMISSION_ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun setLastKnownLocation() {
        LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener {
            searchViewModel.setPostcode(it.longitude, it.latitude, this)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.preferences -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.archive -> {
                val intent = Intent(this, ArchiveActivity::class.java)
                startActivity(intent)
            }
            R.id.about ->{
                val intent = Intent(this, About::class.java)
                startActivity(intent)
            }
            R.id.share -> shareApp()
        }
        return true
    }

    private fun shareApp() {
        val appNameId = this.applicationInfo.labelRes
        val packageName = this.packageName
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(appNameId))
        val shareTitle = "Try Bull Sheet Generator "
        val shareLink = "https://play.google.com/store/apps/details?id=$packageName"
        sendIntent.putExtra(Intent.EXTRA_TEXT, "$shareTitle $shareLink")
        startActivity(Intent.createChooser(sendIntent, "Share link via"))
    }

}
package cc.popkorn.samples.androidx.viewmodel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cc.popkorn.androidx.viewModel.viewModel
import cc.popkorn.annotations.Injectable
import cc.popkorn.core.Propagation
import cc.popkorn.core.Scope

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    private val viewModel: FirstViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.greeting.observe(
            this,
            { greeting -> findViewById<TextView>(R.id.textViewGreeting).text = greeting },
        )

        findViewById<Button>(R.id.buttonNext).setOnClickListener {
            val name = findViewById<EditText>(R.id.editTextName).text.toString()
            val intent = Intent(this, SecondActivity::class.java).apply { putExtra("name", name) }
            startActivity(intent)
        }
    }
}

@Injectable(scope = Scope.BY_NEW, propagation = Propagation.NONE)
class FirstViewModel : ViewModel() {

    private val _greeting: MutableLiveData<String> = MutableLiveData()
    val greeting: LiveData<String> = _greeting

    init {
        _greeting.value = getGreeting()
    }

    private fun getGreeting(): String = "Hello!".also { Log.v("PopKornViewModel", "Requested") }
}

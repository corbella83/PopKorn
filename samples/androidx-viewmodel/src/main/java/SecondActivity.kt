package cc.popkorn.samples.androidx.viewmodel

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cc.popkorn.androidx.viewModel.createViewModel
import cc.popkorn.annotations.Injectable
import cc.popkorn.core.Propagation
import cc.popkorn.core.Scope

class SecondActivity : AppCompatActivity(R.layout.second_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nameArg: String = intent.getStringExtra("name") ?: "Name not found"

        val viewModel: NameViewModel = createViewModel { add(nameArg) }

        viewModel.name.observe(this, { name ->
            findViewById<TextView>(R.id.textViewName).text = name
        })
    }
}

@Injectable(scope = Scope.BY_NEW, propagation = Propagation.NONE)
class NameViewModel(private val nameArg: String) : ViewModel() {

    private val _name: MutableLiveData<String> = MutableLiveData()
    val name: LiveData<String> = _name

    init {
        _name.value = getUserName()
    }

    private fun getUserName(): String = nameArg.also { Log.v("PopKornViewModel", "Requested") }
}

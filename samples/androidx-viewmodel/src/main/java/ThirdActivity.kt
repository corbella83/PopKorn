package cc.popkorn.samples.androidx.viewmodel

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cc.popkorn.androidx.viewModel.sharedViewModel
import cc.popkorn.annotations.Injectable
import cc.popkorn.core.Propagation
import cc.popkorn.core.Scope

class ThirdActivity : AppCompatActivity(R.layout.third_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction().replace(R.id.fragmentA, FragmentA()).commit()
        supportFragmentManager.beginTransaction().replace(R.id.fragmentB, FragmentB()).commit()
    }
}

class FragmentA : Fragment(R.layout.fragment) {

    private val viewModel: ThirdViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.buttonChangeName).setOnClickListener {
            val name = view.findViewById<EditText>(R.id.editTextName).text.toString()
            viewModel.getGreeting(name)
        }
        viewModel.greeting.observe(
            viewLifecycleOwner,
            { name -> view.findViewById<TextView>(R.id.textViewFragment).text = name },
        )
    }
}

class FragmentB : Fragment(R.layout.fragment) {

    private val viewModel: ThirdViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.buttonChangeName).setOnClickListener {
            val name = view.findViewById<EditText>(R.id.editTextName).text.toString()
            viewModel.getGreeting(name)
        }

        viewModel.greeting.observe(
            viewLifecycleOwner,
            { name -> view.findViewById<TextView>(R.id.textViewFragment).text = name },
        )
    }
}

@Injectable(scope = Scope.BY_HOLDER, propagation = Propagation.NONE)
class ThirdViewModel : ViewModel() {

    private val _greeting: MutableLiveData<String> = MutableLiveData()
    val greeting: LiveData<String> = _greeting

    init {
        getGreeting("Javi")
    }

    fun getGreeting(name: String) {
        _greeting.value = "Hello, $name!".also { Log.v("PopKornViewModel", "Requested") }
    }
}

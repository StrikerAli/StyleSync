import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliashraf.project.R

class RecyclerViewAdapter1(private val dataSet: List<String>) :
    RecyclerView.Adapter<RecyclerViewAdapter1.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Add your ViewHolder components here
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_userprofile, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind your data to the ViewHolder components here
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}

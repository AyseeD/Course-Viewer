package com.app.course_viewer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.app.course_viewer.databinding.ItemCourseBinding
import com.app.course_viewer.model.Course
import java.util.*
import kotlin.collections.ArrayList

//the class for recyclerview filter and sorts adapter
class CourseAdapter(
    private val originalList: List<Course>,
    private val itemClick: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>(), Filterable {

    private val filteredList = ArrayList<Course>().apply { addAll(originalList) }

    private var semesterFilter: String = "All" //has all, fall and spring
    private var searchQuery: String = "" //for searching by string for code or course name
    private var sortMode: SortMode = SortMode.NONE //for sorting code in asceending, or credits in descending

    enum class SortMode { NONE, CODE_ASC, CREDITS_DESC }

    inner class CourseViewHolder(private val binding: ItemCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {
            //gives the info needed for the course displaying in the recyclerview
        fun bind(course: Course) {
            binding.tvCourseCode.text = course.code
            binding.tvCourseName.text = course.name
            binding.tvCourseMeta.text = "${course.credits} • ${course.semester}"
            binding.tvCourseDesc.text = course.description;
            binding.root.setOnClickListener { itemClick(course) }
        }
    }

    //called only when recyclerView needs a new item, item_course.xml is turned to a real view obj
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    //fills the row with actual data by connecting data to ui
    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    //setters for filter, sort, search
    fun setSemesterFilter(semester: String) {
        semesterFilter = semester
        filter.filter(searchQuery)
    }

    fun setSortMode(mode: SortMode) {
        sortMode = mode
        applySort()
        notifyDataSetChanged()
    }

    fun setSearchQuery(query: String) {
        searchQuery = query
        filter.filter(searchQuery)
    }

    private fun applySort() {
        when (sortMode) {
            SortMode.NONE -> {} //keep current
            SortMode.CODE_ASC -> filteredList.sortWith(compareBy { normalizeCourseCode(it.code) })
            SortMode.CREDITS_DESC -> filteredList.sortWith(compareByDescending { it.credits })
        }
    }

    private fun normalizeCourseCode(code: String): String {
        val parts = code.trim().split("\\s+".toRegex(), 2)
        return if (parts.size == 2) {
            val dept = parts[0]
            val rest = parts[1]
            val num = rest.filter { it.isDigit() }
            dept + String.format("%05d", num.toIntOrNull() ?: 0)
        } else code
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val q = constraint?.toString()?.lowercase(Locale.getDefault()) ?: ""
                val temp = ArrayList<Course>()
                for (c in originalList) {
                    //semester check
                    if (semesterFilter != "All" && !c.semester.equals(semesterFilter, true)) {
                        continue
                    }
                    //search check
                    if (q.isNotEmpty()) {
                        val inCode = c.code.lowercase(Locale.getDefault()).contains(q)
                        val inName = c.name.lowercase(Locale.getDefault()).contains(q)
                        if (!inCode && !inName) continue
                    }
                    temp.add(c)
                }

                when (sortMode) {
                    SortMode.CODE_ASC -> temp.sortWith(compareBy { normalizeCourseCode(it.code) })
                    SortMode.CREDITS_DESC -> temp.sortWith(compareByDescending { it.credits })
                    else -> {}
                }

                val results = FilterResults()
                results.values = temp
                results.count = temp.size
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList.clear()
                if (results?.values is List<*>) {
                    filteredList.addAll(results.values as List<Course>)
                }
                notifyDataSetChanged()
            }
        }
    }
}

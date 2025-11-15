package com.app.course_viewer.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.course_viewer.adapter.CourseAdapter
import com.app.course_viewer.databinding.ActivityMainBinding
import com.app.course_viewer.model.Course
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CourseAdapter
    private lateinit var courses: List<Course>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //list of example courses
        courses = createStaticCourses()

        //adapter for example courses
        adapter = CourseAdapter(courses) { course ->
            showCourseDetails(course)
        }

        binding.rvCourses.layoutManager = LinearLayoutManager(this)
        binding.rvCourses.adapter = adapter

        setupSemesterSpinner()
        setupSortSpinner()
        setupSearchView()
    }

    //create example courses
    private fun createStaticCourses(): List<Course> {
        return listOf(
            Course("CENG 101", "Introduction to Programming", 4, "Fall", "Learn programming languages and terms"),
            Course("CENG 210", "Data Structures", 3, "Spring","Learn data structures with C++"),
            Course("CENG 330", "Operating Systems", 4, "Fall","Learn Operating systems of computers"),
            Course("CENG 343", "Mobile Applications", 3, "Spring","Learn mobil application making on android studio"),
            Course("MATH 201", "Linear Algebra", 3, "Fall","Learn linear algebra formulas"),
            Course("PHYS 101", "Physics I", 4, "Spring","Learn AP physics"),
            Course("CENG 443", "Software Engineering", 3, "Fall","Learn conventions of software engineering"),
            Course("ELEC 220", "Digital Systems", 3, "Spring","Learn different digital systems"),
            Course("CENG 421", "Computer Networks", 3, "Fall","Learn the layers of network"),
            Course("STAT 250", "Probability & Statistics", 3, "Spring","Learn probability calculation"),
            Course("CIVL 110", "Engineering Drawing", 2, "Fall","Learn drawing for engineers")
        )
    }

    private fun setupSemesterSpinner() {
        val options = listOf("All", "Fall", "Spring")
        val adapterArray = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        binding.spinnerSemester.adapter = adapterArray
        binding.spinnerSemester.setSelection(0)
        binding.spinnerSemester.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                val sel = options[position]
                adapter.setSemesterFilter(sel)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }

    private fun setupSortSpinner() {
        val sortOptions = listOf("None", "Code", "Credits")
        val sortAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortOptions)
        binding.spinnerSort.adapter = sortAdapter
        binding.spinnerSort.setSelection(0)
        binding.spinnerSort.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> adapter.setSortMode(CourseAdapter.SortMode.NONE)
                    1 -> adapter.setSortMode(CourseAdapter.SortMode.CODE_ASC)
                    2 -> adapter.setSortMode(CourseAdapter.SortMode.CREDITS_DESC)
                }
                // re-run filter so sorting applies to current filtered set
                adapter.setSearchQuery(binding.searchView.query?.toString() ?: "")
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }

    private fun setupSearchView() {
        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.setSearchQuery(query ?: "")
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    //for the little alertdialog box
    private fun showCourseDetails(course: Course) {
        val msg = """
            - Code: ${course.code}
            - Name: ${course.name}
            - Credits: ${course.credits}
            - Semester: ${course.semester}
            - Description: ${course.description}
        """.trimIndent()

        MaterialAlertDialogBuilder(this)
            .setTitle("\uD83E\uDEB7 ${course.code}")
            .setMessage(msg)
            .setPositiveButton("Close", null)
            .show()
    }
}

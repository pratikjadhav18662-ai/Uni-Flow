package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Course
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("UniFlow", appName)
  }

  @Test
  fun `test attendance bunk calculation`() {
    val course = Course(
      id = 1,
      name = "Data Structures",
      code = "CS201",
      instructor = "Dr. Thorne",
      location = "Hall B",
      colorHex = "#4F46E5",
      targetAttendancePercent = 75,
      attendedClasses = 18,
      totalClasses = 20
    )

    // Attended: 18/20 = 90%
    assertTrue(course.isAttendanceSafe)
    assertEquals(90.0f, course.attendancePercentage, 0.01f)
    // 18 / 0.75 = 24. 24 - 20 = 4 safe bunks
    assertEquals(4, course.safeBunks())
  }

  @Test
  fun `test user creation model`() {
    val user = com.example.data.model.User(
      fullName = "Maya Johnson",
      email = "maya@campus.edu",
      studentId = "UF-2024-9901",
      passwordHash = "securepass123"
    )
    assertEquals("Maya Johnson", user.fullName)
    assertEquals("maya@campus.edu", user.email)
    assertEquals("UF-2024-9901", user.studentId)
  }
}

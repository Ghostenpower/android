package com.example.jixiv

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.jixiv.utils.User
import com.example.jixiv.viewModel.UserViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class UserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: UserViewModel
    private lateinit var userObserver: Observer<User?>

    @Before
    fun setup() {
        viewModel = UserViewModel()
        userObserver = mock(Observer::class.java) as Observer<User?>
        viewModel.user.observeForever(userObserver)
    }

    @Test
    fun testLogin() {
        val user = User(
            appKey = "appKey123",
            id = 1L,
            username = "JohnDoe",
            password = "password",
            sex = 1,
            introduce = "Hello",
            avatar = "avatar.png",
            createTime = System.currentTimeMillis(),
            lastUpdateTime = System.currentTimeMillis()
        )
        viewModel.login(user)

        verify(userObserver).onChanged(user)
        assertEquals(user, viewModel.user.value)
    }

    @Test
    fun testLogout() {
        viewModel.logout()

        verify(userObserver).onChanged(null)
        assertEquals(null, viewModel.user.value)
    }
}

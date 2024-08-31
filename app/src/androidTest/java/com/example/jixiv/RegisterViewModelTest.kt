import com.example.jixiv.viewModel.RegisterViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        viewModel = RegisterViewModel()
    }

    @Test
    fun `updateUsername updates username correctly`() = runTest {
        val newUsername = "newUser"
        viewModel.updateUsername(newUsername)
        assertEquals(newUsername, viewModel.username.value)
    }

    @Test
    fun `updatePassword updates password correctly`() = runTest {
        val newPassword = "newPass"
        viewModel.updatePassword(newPassword)
        assertEquals(newPassword, viewModel.password.value)
    }

    @Test
    fun `updateRePassword updates repassword correctly`() = runTest {
        val newRePassword = "newRePass"
        viewModel.updateRePassword(newRePassword)
        assertEquals(newRePassword, viewModel.repassword.value)
    }
}

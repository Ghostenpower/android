import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.jixiv.utils.Share
import com.example.jixiv.viewModel.ShareViewModel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class ShareViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val viewModel: ShareViewModel = ShareViewModel()

    @Test
    fun testUpdateSelectedTab() {
        val observer: Observer<Int> = mock()
        viewModel.selectedTab.observeForever(observer)

        viewModel.updateSelectedTab(1)

        verify(observer).onChanged(1)
        assertEquals(1, viewModel.selectedTab.value)
    }

    @Test
    fun testAddShares() {
        // 假设 Share 类的构造函数需要所有属性
        val initialShares = listOf(
            Share(1, 1, "Content1", 1234567890, false, false, true, 1, 1, listOf("url1"), 1, 1, 1, "Title1", "User1", "Avatar1"),
            Share(2, 2, "Content2", 1234567890, false, false, false, 2, 2, listOf(), 2, 2, 2, "Title2", "User2", "Avatar2")
        )
        val newShares = listOf(
            Share(3, 3, "Content3", 1234567890, false, true, true, 3, 3, listOf("url2"), 3, 3, 3, "Title3", "User3", "Avatar3"),
            Share(4, 4, "Content4", 1234567890, true, false, false, 4, 4, listOf("url3"), 4, 4, 4, "Title4", "User4", "Avatar4")
        )
        viewModel.updateShares(initialShares)
        viewModel.addShares(newShares)

        val updatedShares = viewModel.shares.value
        assertNotNull(updatedShares)
        assertEquals(3, updatedShares?.size) // 4 是初始和新分享的总数
    }
}

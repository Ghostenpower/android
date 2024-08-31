import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.jixiv.utils.Share
import com.example.jixiv.viewModel.MyViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class MyViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MyViewModel
    private lateinit var selectListObserver: Observer<List<Boolean>>
    private lateinit var sharesObserver: Observer<List<Share>>
    private lateinit var isEditObserver: Observer<Boolean>

    @Before
    fun setup() {
        viewModel = MyViewModel()
        selectListObserver = mock(Observer::class.java) as Observer<List<Boolean>>
        sharesObserver = mock(Observer::class.java) as Observer<List<Share>>
        isEditObserver = mock(Observer::class.java) as Observer<Boolean>

        viewModel.selectList.observeForever(selectListObserver)
        viewModel.shares.observeForever(sharesObserver)
        viewModel.isEdit.observeForever(isEditObserver)
    }

    @Test
    fun testResetSelectList() {
        val shares = listOf(
            Share(1L, 0, "", 0L, false, false, false, 1L, 0L, emptyList(), 0L, 0, 0L, "", "", ""),
            Share(2L, 0, "", 0L, false, false, false, 2L, 0L, emptyList(), 0L, 0, 0L, "", "", "")
        )
        viewModel.updateShares(shares)
        assertEquals(listOf(false, false), viewModel.selectList.value)
    }

    @Test
    fun testUpdateSelectList() {
        val shares = listOf(
            Share(1L, 0, "", 0L, false, false, false, 1L, 0L, emptyList(), 0L, 0, 0L, "", "", "")
        )
        viewModel.updateShares(shares)
        viewModel.updateSelectList(0, true)
        assertEquals(listOf(true), viewModel.selectList.value)
    }

    @Test
    fun testUpdateShares() {
        val shares = listOf(
            Share(1L, 0, "", 0L, false, false, false, 1L, 0L, listOf("image1.png"), 0L, 0, 0L, "", "", "")
        )
        viewModel.updateShares(shares)
        verify(sharesObserver).onChanged(shares)
        assertEquals(1, viewModel.selectList.value?.size)
    }

    @Test
    fun testUpdateSelectedTab() {
        viewModel.updateSelectedTab(true)
        assertEquals(true, viewModel.isEdit.value)
    }
}

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import android.net.Uri
import com.example.jixiv.utils.Share
import com.example.jixiv.viewModel.EditViewModel
import org.junit.Before

class EditViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: EditViewModel

    @Before
    fun setUp() {
        viewModel = EditViewModel()
    }

    @Test
    fun `addUriToUriList adds URI correctly`() {
        val uri = Uri.parse("content://example/uri")
        viewModel.addUriToUriList(uri)
        assertEquals(listOf(uri), viewModel.imgUriList.value)
    }

    @Test
    fun `deleteUriFromList removes URI correctly`() {
        val uri = Uri.parse("content://example/uri")
        viewModel.addUriToUriList(uri)
        viewModel.deleteUriFromList(0)
        assertEquals(emptyList<Uri>(), viewModel.imgUriList.value)
    }

    @Test
    fun `updateShare updates share correctly`() {
        val share = Share(1L, 2, "title", 3L, false, false, false, 4L, 5L, emptyList(), 6L, 7, 8L, "content", "imageCode", "id")
        viewModel.updateShare(share)
        assertEquals(share, viewModel.share.value)
    }
}



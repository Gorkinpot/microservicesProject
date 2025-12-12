import com.example.Service.DocumentRepository
import com.example.Service.DocumentService
import com.example.dto.request.DocumentAddingRequest
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import kotlin.test.Test

class DocumentServiceUnitTest {

    @Test
    fun `addDocument should call repository`() {
        mockkObject(DocumentRepository)

        val request = DocumentAddingRequest(userId = 1, document = "passport.pdf")
        every { DocumentRepository.addDocument(request) } returns Unit

        DocumentService.addDocument(request)

        verify { DocumentRepository.addDocument(request) }
    }
}

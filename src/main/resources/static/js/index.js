const fileInput = document.getElementById('file');
const fileNameDisplay = document.getElementById('file-name');
const uploadForm = document.getElementById('upload-form');
const errorMessage = document.getElementById('error-message');
const toast = document.getElementById('toast');

fileInput.addEventListener('change', () => {
    const fileName = fileInput.files[0]?.name || "no file selected";
    fileNameDisplay.textContent = fileName;
});

uploadForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const formData = new FormData(uploadForm);

    try {
        const response = await fetch(uploadForm.action, {
            method: 'POST',
            body: formData,
        });

        if (!response.ok) {
            const errorText = await response.text();
            errorMessage.textContent = errorText || "an error occurred while uploading the file";
            errorMessage.style.display = "block";
            showToast("file upload failed");
        } else {
            errorMessage.style.display = "none";
            showToast("file uploaded successfully!");
            uploadForm.reset();
            fileNameDisplay.textContent = "no file selected";
        }
    } catch (error) {
        errorMessage.textContent = "failed to upload. try again.";
        errorMessage.style.display = "block";
        showToast("file upload failed.");
    }
});

function showToast(message) {
    toast.textContent = message;
    toast.classList.add('show');

    setTimeout(() => {
        toast.classList.remove('show');
    }, 5000);
}

const fileInput = document.getElementById('file');
const fileNameDisplay = document.getElementById('file-name');
const uploadForm = document.getElementById('upload-form');
const toast = document.getElementById('toast');
const confirmPrintModal = document.getElementById('confirm-print-modal');
const confirmPrintButton = document.getElementById('confirm-print-button');
const cancelButton = document.getElementById('cancel-button');

let currentDocId = null;
let currentFileName = null;
const serverIp = "192.168.178.104";
const serverPort = "8080";

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
            showToast(errorText || "failed to upload the file.");
        } else {
            const result = await response.json();
            currentFileName = result.fileName;
            currentDocId = result.docId;
            uploadForm.reset();
            fileNameDisplay.textContent = "no file selected";
            showPrintModal();
        }
    } catch (error) {
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

function showPrintModal() {
    const modalMessage = document.getElementById('modal-message');
    modalMessage.textContent = `do you want to print "${currentFileName}"?`;
    confirmPrintModal.style.display = "flex";
}

confirmPrintButton.addEventListener('click', () => {
    confirmPrintModal.style.display = "none";
    showToast("printing initiated...");
    executePrint(true);
});

cancelButton.addEventListener('click', () => {
    confirmPrintModal.style.display = "none";
    showToast("print cancelled!");
    executePrint(false);
});

async function executePrint(shouldPrint) {
    if (!currentDocId || !currentFileName) {
        showToast("no document selected for printing");
        return;
    }

    const printRequest = {
        docId: currentDocId,
        print: shouldPrint,
    };

    try {
        const response = await fetch(`http://${serverIp}:${serverPort}/api/v1/print`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(printRequest),
        });

        if (!response.ok) {
            const errorText = await response.text();
            showToast(errorText || "failed to print the file.");
        } else {
            const responseText = await response.text();
            showToast(responseText);
        }
    } catch (error) {
        showToast("printing failed");
    }
}

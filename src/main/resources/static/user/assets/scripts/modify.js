const modifyModal = document.getElementById('modifyContent');
const modifyForm = document.querySelector('[data-name="modifyForm"]');
const cancelButton = modifyForm.querySelector('[name="cancel"]');
const closeIcon = modifyForm.querySelector('.closeIcon');

const closeModal = () => {
    modifyModal.removeAttribute('data-visible');
}

cancelButton.onclick = closeModal;
closeIcon.onclick = closeModal;



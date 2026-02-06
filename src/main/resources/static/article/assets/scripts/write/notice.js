/** @type {HTMLFormElement} */
const $writeForm = document.forms['writeForm'];

$writeForm.addEventListener('submit', (e) => {
    e.preventDefault();

    if ($writeForm['title'].value === '') {
        alert('제목');
        return;
    }

    if ($writeForm['content'].value === '') {
        alert('내용');
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData($writeForm);

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;
        if (xhr.status < 200 || xhr.status >= 400) {
            alert('에러');
            return;
        }
        const response = JSON.parse(xhr.responseText);
        if (response.result === 'SUCCESS') {
            location.href = `/article/notice?id=${response.id}`;
        } else {
            alert('저장 실패');
        }
    };

    xhr.open('POST', location.pathname);
    xhr.send(formData);
});

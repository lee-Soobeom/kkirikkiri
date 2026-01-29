/** @type {HTMLFormElement}*/
const $serviceForm = document.forms['serviceForm'];

$serviceForm.addEventListener('submit', (e) => {
    e.preventDefault();

    if ($serviceForm['question'].value === '') {
        alert('질문 내용 빈칸');
        $serviceForm['question'].focus();
        return;
    }

    if (!/^[\s\S]{1,50}$/g.test($serviceForm['question'].value)) {
        alert('질문 내용 양식');
        $serviceForm['question'].focus();
        $serviceForm['question'].select();
        return;
    }

    if ($serviceForm['answer'].value === '') {
        alert('질문 답변 빈칸');
        $serviceForm['answer'].focus();
        return;
    }

    if (!/^[\s\S]{1,1000}$/g.test($serviceForm['answer'].value)) {
        alert('질문 답변 양식');
        $serviceForm['answer'].focus();
        $serviceForm['answer'].select();
        return;
    }

    if ($serviceForm['filter'].value === -1) {
        alert('필터 선택');
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("question", $serviceForm['question'].value);
    formData.append("answer", $serviceForm['answer'].value);
    formData.append("filter", $serviceForm['filter'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 400) {
            alert(`요청 문제 ${xhr.status}`);
            return;
        }
        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case 'SUCCESS':
                location.href = '/board/service';
                break;
            case 'FAILURE':
                alert('작성 실패');
                break;
        }

    };
    xhr.open('POST', '/service/write');
    xhr.send(formData);

})
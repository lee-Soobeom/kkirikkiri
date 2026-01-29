/** @type {HTMLElement} */
const $main = document.getElementById('main');
const $questions = Array.from($main.querySelectorAll(':scope > .question-container > .question'));

if ($questions.length !== 0) {
    $questions.forEach(($question) => {
        $question.addEventListener('click', () => {
            const id = $question.dataset['id'];
            location.href = `/service/?id=${id}`;
        });
    });
}
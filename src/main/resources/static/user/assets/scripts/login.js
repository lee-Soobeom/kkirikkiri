const $loginForm = document.forms['loginForm'];
const $emailInput = $loginForm['email'];
const $passwordInput = $loginForm['password'];
const $rememberCheck = $loginForm['remember'];

const showWarning = ($input, message) => {
   const $label = $input.closest('.label');
   const $message = $label.querySelector('[data-component="label.message"]');
   $message.querySelector('.text').innerText = message;
   $message.classList.add('visible');
   $input.focus();
}

const hideWarning = ($input) => {
   const $label = $input.closest('.label');
   const $message = $label.querySelector('[data-component="label.message"]');
   $message.classList.remove('visible');
}

$emailInput.addEventListener('input', () => {
   const value = $emailInput.value;
   const emailRegex = /^(?=.{8,50}$)([\da-zA-Z_.]{4,25})@([\da-z\-]+\.)?([\da-z\-]{2,})\.([a-z]{2,15}\.)?([a-z]{2,3})$/

   if (value.length === 0) {
      showWarning($emailInput, '이메일을 입력해 주세요');
   } else if (!emailRegex.test(value)) {
      showWarning($emailInput, '올바른 이메일 형식이 아닙니다.');
   } else {
      hideWarning($emailInput);
   }
});

$passwordInput.addEventListener('input', () => {
   const value = $passwordInput.value;
   const passwordRegex = /^[\da-zA-Z`~!@#$%^&*()\-_=+\[{\]}\\|;:'",<.>\/?]{8,50}$/

   if (value.length > 0) {
      hideWarning($passwordInput);
   }
   if (passwordRegex.test(value)) {
      hideWarning($passwordInput);
   }
});

$loginForm.addEventListener('submit', (e) => {
   e.preventDefault();

   if ($emailInput.value === '') {
      showWarning($emailInput, '이메일을 입력해 주세요');
      $emailInput.focus();
      return;
   }

   if ($passwordInput.value === '') {
      showWarning($passwordInput, '비밀번호를 입력해 주세요');
      return;
   }

   const formData = new FormData($loginForm);

   fetch('/user/login', {
      method: 'POST',
      body: formData
   }).then(response => {
      return response.json();
   }).then(data => {
      if (data.result === 'SUCCESS') {
         if ($rememberCheck.checked) {
            localStorage.setItem('savedEmail', $emailInput.value);
         } else {
            localStorage.removeItem('savedEmail');
         }
         location.href = '/';
      } else {
         //showWarning($passwordInput, '이메일 또는 비밀번호가 올바르지 않습니다') TODO modal 생성 후 만들기
         alert('로그인 실패')
      }
   }).catch(error => {/*여기도 모달 생성 후에 만들기*/
      console.error('로그인 에러:', error);
      alert('서버와 통신 중 문제가 발생했습니다.');
   })

});

window.addEventListener('DOMContentLoaded', () => {
   const savedEmail = localStorage.getItem('savedEmail');
   if (savedEmail) {
      $emailInput.value = savedEmail;
      $rememberCheck.checked = true;
   }
});
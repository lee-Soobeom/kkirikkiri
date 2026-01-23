document.addEventListener('DOMContentLoaded', () => {
   const switchButtons = document.querySelectorAll('.switch-btn');
   const forms = document.querySelectorAll('.form');
    const birthYearSelects = document.querySelectorAll('select[name="birthYear"]');
    const birthMonthSelects = document.querySelectorAll('select[name="birthMonth"]');
    const birthDaySelects = document.querySelectorAll('select[name="birthDay"]');
    const currentYear = new Date().getFullYear();

    birthYearSelects.forEach(select => {
       for (let i = currentYear; i >= 1950; i--) {
           const option = document.createElement('option');
           option.value = i;
           option.textContent = i;
           select.append(option);
       }
    });

    birthMonthSelects.forEach(select => {
        for (let i = 1; i <= 12; i++) {
            const option = document.createElement('option');
            const monthValue = String(i).padStart(2, '0');
            option.value = monthValue;
            option.textContent = `${i}월`;
            select.appendChild(option);
        }
    });

    birthDaySelects.forEach(select => {
        for (let i = 1; i <= 31; i++) {
            const option = document.createElement('option');
            const dayValue = String(i).padStart(2, '0');
            option.value = dayValue;
            option.textContent = `${i}일`;
            select.appendChild(option);
        }
    });

   switchButtons.forEach(button => {
       button.addEventListener('click', () => {
           const type = button.getAttribute('data-type');

           switchButtons.forEach(btn => btn.classList.remove('active'));
           button.classList.add('active');

           forms.forEach(form => {form.removeAttribute('data-visible');
               form.scrollTop = 0;});

           const targetForm = document.querySelector(`.form[data-type="${type}"]`);
           if (targetForm) {
               targetForm.setAttribute('data-visible', '');
               targetForm.scrollTop = 0;
           }
       });
   });

    const defaultButton = document.querySelector('.switch-btn[data-type="common"]');
    if (defaultButton) {
        defaultButton.click();
    }

    const cancelButtons = document.querySelectorAll('button[name="cancel"]');
    cancelButtons.forEach(button => {
        button.addEventListener('click', () => {
           if (confirm('가입을 취소하시겠습니까? 입력한 정보는 저장되지 않습니다.')) {
               location.href = '/user/login';
           }
        });
    });

    const allForms = document.querySelectorAll('.form');
    allForms.forEach(form => {
       form.addEventListener('submit', (e) => {
           e.preventDefault();

           const requiredTerms = form.querySelectorAll(':scope > .term > input[type="checkbox"][required]');
           const allChecked = Array.from(requiredTerms).every(checkbox => checkbox.checked);
           if (!allChecked) {
               alert('모든 필수 약관에 동의해 주세요.');
               return;
           }

           alert('회원 가입이 완료되었습니다!');
           location.href = '/';
       });
    });

});


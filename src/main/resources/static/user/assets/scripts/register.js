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
           option.textContent = `${i}년`;
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

    const regexMap = {
        email: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
        contact: /^010\d{8}$/,
        businessNumber: /^\d{10}$/,
        name: /^[가-힣]{2,5}$/,
        bossName: /^[가-힣]{2,5}$/,
        nickname: /^[a-zA-Z0-9가-힣\s]{2,15}$/,
        storeName: /^[a-zA-Z0-9가-힣\s]{2,15}$/,
        storeContact: /^0\d{8,10}$/,
        businessType: /^[가-힣]{2,10}$/,
        password: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{8,}$/
    }

    const validateRules = {
        name: {
            regex: regexMap.name,
            empty: "성함을 입력해 주세요",
            invalid: "2 ~ 5자 한글 실명을 입력해 주세요"
        },
        email: {
            regex: regexMap.email,
            empty: "이메일을 입력해 주세요",
            invalid: "올바른 이메일 형식이 아닙니다. 유효한 이메일 형식을 사용해 주세요"
        },
        password: {
            regex: regexMap.password,
            empty: "비밀번호를 입력해 주세요",
            invalid: "8글자 이상 숫자, 영문자와 특수기호 하나를 혼합하여 입력해 주세요"
        },
        businessNumber: {
            regex: regexMap.businessNumber,
            empty: "사업자 등록 번호를 입력해 주세요",
            invalid: "숫자 10자리를 정확히 입력해 주세요"
        },
        nickname: {
            regex: regexMap.nickname,
            empty: "닉네임을 입력해 주세요",
            invalid: "특수문자를 제외하고 2 ~ 15자 이내로 입력해 주세요"
        },
        storeName: {
            regex: regexMap.storeName,
            empty: "가게 이름을 입력해 주세요",
            invalid: "특수문자를 제외하고 2 ~ 15자 이내로 입력해 주세요"
        },
        contact: {
            regex: regexMap.contact,
            empty: "휴대폰 번호를 입력해 주세요",
            invalid: "'-' 없이 010으로 시작하는 11자리 숫자를 입력해 주세요"
        },
        storeContact: {
            regex: regexMap.storeContact,
            empty: "가게 전화번호를 입력해 주세요",
            invalid: "올바른 전화번호 형식이 아닙니다 (예: 021234567)"
        },
        businessType: {
            regex: regexMap.businessType,
            empty: "업종을 입력해 주세요",
            invalid: "한글 2 ~ 10자 이내로 입력해 주세요 (예: 음식점, 카페)"
        },
        bossName: {
            regex: regexMap.bossName,
            empty: "사장님 성함을 입력해 주세요",
            invalid: "한글 2 ~ 5자 한글 실명을 입력해 주세요"
        }
    }

    const handleInputInvalidation = (e) => {
        const warningInput = e.target;
        const warningRule = validateRules[warningInput.name];
        if (!warningRule) return;

        const warningLabel = warningInput.closest('.label');
        const warningValue = warningInput.value.trim();

        if (warningValue === "") {
            toggleMessage(warningLabel, false)
        } else if (warningRule.regex && !warningRule.regex.test(warningValue)) {
            toggleMessage(warningLabel, true, warningRule.invalid);
        } else {
            toggleMessage(warningLabel, false);
        }
    }

    document.querySelectorAll('.field').forEach(field => {
        field.addEventListener('input', handleInputInvalidation);
    });

    document.querySelectorAll('input[name="passwordCheck"]').forEach(checkInput => {
        checkInput.addEventListener('input', (e) => {
            const form = e.target.closest('.form');
            const passwordValue = form['password'].value;
            const label = e.target.closest('.label');
            const isMismatch = (passwordValue !== e.target.value) && (e.target.value !== "");
            toggleMessage(label, isMismatch, "비밀번호가 일치하지 않습니다.")
        });
    });

    forms.forEach(form => {
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            let firstErrorField = null;

            const inputs = form.querySelectorAll('.field[required], .field[name]');
            inputs.forEach(input => {
                const rule = validateRules[input.name];
                const label = input.closest('.label');
                const value = input.value.trim();

                if (!label) return;

                if (value === "") {
                    toggleMessage(label, true, rule?.empty || "필수 항목입니다.");
                    if (!firstErrorField) firstErrorField = input;
                } else if (rule && rule.regex && !rule.regex.test(value)) {
                    toggleMessage(label, true, rule.invalid);
                    if (!firstErrorField) firstErrorField = input;
                }
            });

            const password = form['password'];
            const passwordCheck = form['passwordCheck'];
            if (password && passwordCheck && password.value !== passwordCheck.value) {
                toggleMessage(passwordCheck.closest('.label'), true, "비밀번호가 일치하지 않습니다.");
                if (!firstErrorField) firstErrorField = passwordCheck;
            }

            const requiredTerms = form.querySelectorAll('.term input[required]');
            const allChecked = Array.from(requiredTerms).every(checkBox => checkBox.checked);
            if (!allChecked) {
                alert('모든 필수 약관에 동의해 주세요.');
                return;
            }

            if (firstErrorField) {
                firstErrorField.focus();
                return;
            }

            const formData = new FormData(form);
            formData.append('type', form.getAttribute('data-type'));

            // if (typeof mtLoading !== 'undefined') {
            //     mtLoading.show();
            // }

            fetch('/user/register', {
                method: 'POST',
                body: formData
            }).then(response => {
               //mtLoading.hide();
                if (!response.ok) throw new Error(response.status.toString());
                return response.json();
            })
                .then(response => {
                    switch (response.result) {
                        case 'SUCCESS':
                            alert('회원가입이 완료되었습니다!');
                            location.href = '/user/login';
                            break;
                        case 'FAILURE_DUPLICATE_EMAIL':
                            alert('이미 사용 중인 이메일입니다.');
                            break;
                        case 'FAILURE':
                            alert('가입 처리 중 알 수 없는 오류가 발생했습니다.');
                            break;
                        default:
                            alert(`서버 응답 오류: ${response.result}`);
                    }
                })
                .catch(error => {
                    //mtLoading.hide();
                    alert(`요청을 전송하는 도중 오류가 발생했습니다. (${error})`);
                });
            });
        });

        const addressButtons = document.querySelectorAll('button[name="addressButton"]');

        addressButtons.forEach(button => {
           button.addEventListener('click', () => {
              const currentForm = button.closest('.form');
              const addressInput = currentForm.querySelector('input[name="address"]');
              const detailInput = currentForm.querySelector('input[name="addressDetail"]');

              new daum.Postcode({
                  oncomplete: function (data) {
                      let addr = '';
                      let extraAddr = '';

                      if (data.userSelectedType === 'R') {
                          addr = data.roadAddress;
                      } else {
                          addr = data.jibunAddress;
                      }

                      addressInput.value = addr;
                      toggleMessage(addressInput.closest('.label'), false);

                      if (detailInput) {
                          detailInput.focus();
                      }
                  }
              }).open();
           });
        });


    });


/**
 * @param {HTMLElement} labelElement
 * @param {boolean} isError
 * @param {string} message*/

function toggleMessage(labelElement, isError, message = "") {
    const infoMessage = labelElement.querySelector('[data-message="info"]');
    const warningMessage = labelElement.querySelector('[data-message="warning"]');

    if (isError) {
        infoMessage?.removeAttribute('data-visible');
        warningMessage?.setAttribute('data-visible', '');

        const warningText = warningMessage?.querySelector('.text');
        if (warningText && message) {
            warningText.innerText = message;
        }
    } else {
        warningMessage?.removeAttribute('data-visible');
        infoMessage?.setAttribute('data-visible', '');
    }
}

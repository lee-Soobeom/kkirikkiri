document.addEventListener('DOMContentLoaded', () => {
    const findButton = document.querySelector('[name="findButton"]');
    const pickupInput = document.getElementById('pickupAddress');

    findButton.addEventListener('click', () => {
        const rect = findButton.getBoundingClientRect();

        const popover = new MtPopoverDialog({
            top: rect.bottom + window.scrollY + 8,
            left: rect.left + window.scrollX
        });

        popover.show(`<div id="postcodeContainer" style="width:360px;height:420px;"></div>`);

        new daum.Postcode({
            oncomplete: data => {
                pickupInput.value = data.address;
                popover.hide();
            }
        }).embed(document.getElementById('postcodeContainer'));
    });
});

/* ================= 공통 Visibility 유틸 ================= */
HTMLElement.VISIBILITY_ATTRIBUTE_NAME = 'data-visible';

HTMLElement.prototype.mtHide = function () {
    this.removeAttribute(HTMLElement.VISIBILITY_ATTRIBUTE_NAME);
    return this;
};

HTMLElement.prototype.mtShow = function () {
    this.setAttribute(HTMLElement.VISIBILITY_ATTRIBUTE_NAME, '');
    return this;
};

/* ================= 중앙 모달 ================= */
class MtDialog {
    $element;
    $modals = [];

    constructor() {
        this.$element = document.createElement('div');
        this.$element.setAttribute('data-object', 'dialog');
        document.body.append(this.$element);
    }

    show({ title, content, isContentHTML = false, buttons = [] }) {
        const $modal = document.createElement('div');
        $modal.setAttribute('data-component', 'dialog.modal');

        const $title = document.createElement('div');
        $title.setAttribute('data-component', 'dialog.modal.title');
        $title.innerText = title;

        const $content = document.createElement('div');
        $content.setAttribute('data-component', 'dialog.modal.content');
        isContentHTML ? $content.innerHTML = content : $content.innerText = content;

        $modal.append($title, $content);

        if (buttons.length > 0) {
            const $btnBox = document.createElement('div');
            $btnBox.setAttribute('data-component', 'dialog.modal.buttonContainer');

            buttons.forEach(btn => {
                const $b = document.createElement('button');
                $b.innerText = btn.caption;
                $b.addEventListener('click', () => btn.onclick?.($modal));
                $btnBox.append($b);
            });

            $modal.append($btnBox);
        }

        this.$element.append($modal);
        this.$element.mtShow();
        setTimeout(() => $modal.mtShow(), 10);

        this.$modals.push($modal);
        return $modal;
    }

    hide($modal) {
        $modal.mtHide();
        setTimeout(() => $modal.remove(), 200);
        this.$element.mtHide();
    }
}

/* ================= 버튼 기준 팝오버 ================= */
class MtPopoverDialog {
    constructor({ top, left }) {
        this.$element = document.createElement('div');
        this.$element.setAttribute('data-object', 'popover-dialog');
        this.$element.style.top = `${top}px`;
        this.$element.style.left = `${left}px`;
        document.body.append(this.$element);
    }

    show(contentHTML) {
        this.$element.innerHTML = contentHTML;
        this.$element.mtShow();
    }

    hide() {
        this.$element.mtHide();
        setTimeout(() => this.$element.remove(), 200);
    }
}

window.mtDialog = new MtDialog();

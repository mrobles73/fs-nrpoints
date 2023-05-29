<script lang="ts" setup>
import { onMounted } from 'vue';
import bootstrap from 'bootstrap/dist/js/bootstrap.min.js';

function setCookie() {
    document.cookie = "cookiesAccept=true; max-age="+60*60*24*30*3;
}

onMounted( () => {
    const checkCookie = document.cookie.indexOf("cookiesAccept=true");
    if(checkCookie === -1) {
        const modal = new bootstrap.Modal("#cookieAlert");
        modal.show();
    }
});

</script>

<template>
    <div class="modal  fade" id="cookieAlert" tabindex="-1" aria-labelledby="cookieAlert" data-bs-backdrop="static" aria-hidden="false" role="dialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content rounded-0 shadow">
            <div class="modal-header border-bottom-0">
                <h1 class="modal-title fs-5">Cookies Consent</h1>
                <!-- <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button> -->
            </div>
            <div class="modal-body py-0">
                <p>This website uses cookies to ensure you get the best experience on our site.</p>
            </div>
            <div class="modal-footer flex-column align-items-stretch w-100 gap-2 pb-3 border-top-0">
                <button type="button" class="btn btn-primary" data-bs-dismiss="modal" @click="setCookie">I Understand</button>
            </div>
            </div>
        </div>
    </div>
</template>

<style>
#cookieAlert .modal-dialog {
    display: flex !important;
    align-items: flex-end !important;
}



</style>
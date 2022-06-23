
const compressBtn = document.querySelector("#compress");
const compressInput = document.querySelector("#compress-input");
const compressResult = document.querySelector("#compress-result");

const decompressBtn = document.querySelector("#decompress");
const decompressInput = document.querySelector("#decompress-input");
const decompressResult = document.querySelector("#decompress-result");

const callApiWithParams = function (path, param, result) {
    return function(){
        if (param.files.length==0){
            result.innerHTML = "Please select the input file";
            return;
        }
        result.innerText = "Loading....";
        const formData = new FormData();
        formData.append('file', param.files[0]);
        fetch(path, {
            method: "POST",
            body: formData
        })
            .then(async (res)=>[res.status, await res.text()])
            .then(([status, res]) => {
                if (status==200){
                    result.innerHTML = '<a href="/download/'+res+'">Download result</a>';
                } else {
                    result.innerHTML = "Error: "+res;
                }

        })
        .catch((error) => {
            console.log(error);
            result.innerHTML("Error: Unknown error");
        });
    }

};

compressBtn.addEventListener("click", callApiWithParams("/compress", compressInput, compressResult));
decompressBtn.addEventListener("click", callApiWithParams("/decompress", decompressInput, decompressResult));
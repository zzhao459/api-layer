import axios from "axios";

const app = axios.create({
    baseURL: process.env.REACT_APP_GATEWAY_URL,
    headers: {
        "Content-type": "application/json",
    },
});

const upload = (url, file, onUploadProgress) => {
    let formData = new FormData();
    formData.append("file", file);
    console.log(formData)
    return app.post("/api/v1/certificate-service/certificate/upload", {
        url: url,
        certificate: formData
    }, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
        onUploadProgress,
    });
};


export default {
    upload,
};

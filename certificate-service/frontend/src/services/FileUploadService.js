import axios from "axios";

const app = axios.create({
    baseURL: process.env.REACT_APP_GATEWAY_URL,
    headers: {
        "Content-type": "application/json",
    },
});

const upload = (label, file, onUploadProgress) => {
    let formData = new FormData();
    formData.append("certificate", file);
    let config = {
        headers: {
            "Content-Type": "multipart/form-data",
        },
        params: {
            certificate: formData,
            label
        },
        onUploadProgress
    }
    return app.post("/api/v1/certificate-service/certificate/upload", formData, config);
};


export default {
    upload,
};

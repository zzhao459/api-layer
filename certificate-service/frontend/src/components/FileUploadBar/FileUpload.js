import React, { useState, useEffect } from "react";
import UploadService from "../../services/FileUploadService";
import "./FileUpload.css"
import '../../assets/css/APIMReactToastify.css';
import { ToastContainer, toast } from 'react-toastify';

const FileUpload = (props) => {
    const [selectedFiles, setSelectedFiles] = useState(undefined);
    const [currentFile, setCurrentFile] = useState(undefined);
    const [progress, setProgress] = useState(0);
    const [message, setMessage] = useState("");
    const [fileInfos, setFileInfos] = useState([]);


    const selectFile = (event) => {
        setSelectedFiles(event.target.files);
    };

    const upload = () => {
        let currentFile = selectedFiles[0];

        setProgress(0);
        setCurrentFile(currentFile);

        console.log(currentFile)
        UploadService.upload(props.label, currentFile, (event) => {
            setProgress(Math.round((100 * event.loaded) / event.total));
        })
            .then((response) => {
                if (response.status === 200) {
                    setMessage("The certificate has been uploaded to the truststore!");
                    toast.success("The certificate has been uploaded to the truststore!", {
                        closeOnClick: true,
                        autoClose: 2000,
                    });
                    toast.dismiss();
                    return response;
                }
            })
            .then((files) => {
                setFileInfos(files.data);
            })
            .catch((error) => {
                setProgress(0);
                setMessage(`Couldn't add the certificate due to the following error: ${error.message}`);
                toast.error(`Couldn't add the certificate due to the following error: ${error.message}`, {
                    closeOnClick: true,
                    autoClose: 2000,
                });
                toast.dismiss();
                setCurrentFile(undefined);
            });

        setSelectedFiles(undefined);
    };

    return (
        <div>
            <ToastContainer />
            {currentFile && (
                <div className="progress">
                    <div
                        className="progress-bar progress-bar-info progress-bar-striped"
                        role="progressbar"
                        aria-valuenow={progress}
                        aria-valuemin="0"
                        aria-valuemax="100"
                        style={{ width: progress + "%" }}
                    >
                        {progress}%
                    </div>
                </div>
            )}

            <label className="btn btn-default">
                <input type="file" onChange={selectFile} />
            </label>

            <button
                className="btn btn-success"
                disabled={!selectedFiles}
                onClick={upload}
            >
                Upload certificate
            </button>

            {/*<div className="alert alert-light" role="alert">*/}
            {/*    {message}*/}
            {/*</div>*/}

            <div className="card">
                <ul className="list-group list-group-flush">
                    {fileInfos &&
                    fileInfos.map((file, index) => (
                        <li className="list-group-item" key={index}>
                            <a href={file.label}>{file.name}</a>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default FileUpload;

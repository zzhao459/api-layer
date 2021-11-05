import React, { useState, useEffect } from "react";
import UploadService from "../../services/FileUploadService";
import "./FileUpload.css"

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
                    return response;
                }
            })
            .then((files) => {
                setFileInfos(files.data);
            })
            .catch((error) => {
                setProgress(0);
                setMessage(`Could not upload the file! Cause: ${error.message}`);
                setCurrentFile(undefined);
            });

        setSelectedFiles(undefined);
    };

    return (
        <div>
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

            <div className="alert alert-light" role="alert">
                {message}
            </div>

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

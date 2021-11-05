import React, { useState, useMemo } from "react";
import { render } from "react-dom";
import Styles from "./Style";
import { useForm, useField } from "react-final-form-hooks";
import Stack from "@mui/material/Stack";
import Button from "@mui/material/Button";
import FileUpload from "../FileUploadBar/FileUpload";
const VerifyTrustedCertificate = () => {
    const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));
    const [alias, setAlias] = useState(null);
    const [errors, setErrors] =  useState(null);
    const onSubmit = async (data) => {
        await sleep(300);
        const url = process.env.REACT_APP_GATEWAY_URL + "/api/v1/certificate-service/certificate/upload";
        let formData = new FormData();

        formData.append('file', data.file[0]);
        fetch(url, {
            method: 'POST',
            mode: 'cors',
            body: JSON.stringify({
                alias: alias,
                certificate: formData
            })
        }).then((response) => {
            if (response.ok) {
                console.log(response);
                alert("Certificate added to the truststore!");
            } else if (!response.ok) {
                throw Error(response.statusText);
            }
        }).catch((error) => {
            alert(error.message);
            setErrors(error.message)
        });
    };

    const validate = values => {
        const errors = {};
        setAlias(values.url);
        if (!values.url) {
            errors.url = "Required";
        }
        return errors;
    };

    const initialValues = useMemo(
        () => ({
            url: "",
            certificate: ""
        }),
        []
    );

    const { register, form, handleSubmit, pristine, submitting} = useForm({
        onSubmit,
        initialValues,
        validate
    });

    const url = useField("url", form);

    return (
        <Styles>
            <h2> Verify trusted certificate panel</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Alias</label>
                    <input
                        {...url.input} placeholder="Certificate Alias" />
                    {url.meta.touched && url.meta.error && (
                        <span>{url.meta.error}</span>
                    )}
                </div>

                <div>
                    <FileUpload label={alias}></FileUpload>
                </div>

                <div className="buttons">
                    <button
                        type="button"
                        onClick={() => form.reset()}
                        disabled={submitting || pristine}
                    >
                        Reset
                    </button>
                </div>
                { errors && (
                    <pre>
                       {errors}
                        <br/>
                        </pre>
                )}
            </form>
        </Styles>
    );

}

export default VerifyTrustedCertificate;



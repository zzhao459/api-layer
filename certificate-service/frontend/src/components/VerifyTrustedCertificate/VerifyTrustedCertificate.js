import React, { useState, useMemo } from "react";
import { render } from "react-dom";
import Styles from "./Style";
import { useForm, useField } from "react-final-form-hooks";

const VerifyTrustedCertificate = () => {
    const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));

    const onSubmit = async values => {
        await sleep(300);
        window.alert(JSON.stringify(values, 0, 2));

        fetch("/certificate", {
            method: 'POST',
            mode: 'cors',
            body: JSON.stringify({
                url: url.input,
                certificate: certificate.input
            })                        
        });
    };

    const validate = values => {
        const errors = {};
        if (!values.url) {
            errors.url = "Required";
        }
        if (!values.lastName) {
            errors.lastName = "Required";
        }
        return errors;
    };

    const initialValues = useMemo(
        () => ({
            url: "https://localhost:8080",
            certificate: ""
        }),
        []
    );

    const { form, handleSubmit, values, pristine, submitting } = useForm({
        onSubmit,
        initialValues,
        validate
    });

    const url = useField("url", form);
    const certificate = useField("certificate", form);

    return (
        <Styles>
            <h2> Verify trusted certificate</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Service URL</label>
                    <input {...url.input} placeholder="First Name" />
                    {url.meta.touched && url.meta.error && (
                        <span>{url.meta.error}</span>
                    )}
                </div>

                <div>
                    <label>Certificate to use</label>
                    <input {...certificate.input} type="textarea" placeholder="Provide base64 encoded certificate." />
                    {certificate.meta.touched && certificate.meta.error && (
                        <span>{certificate.meta.error}</span>
                    )}
                </div>

                <div className="buttons">
                    <button type="submit" onClick={() => onSubmit()}>
                        Verify
                    </button>
                    <button
                        type="button"
                        onClick={() => form.reset()}
                        disabled={submitting || pristine}
                    >
                        Reset
                    </button>
                </div>
                <pre>
                    LOG MESSAGES:
                    <br />
                </pre>
            </form>
        </Styles>
    );

}


export default VerifyTrustedCertificate;



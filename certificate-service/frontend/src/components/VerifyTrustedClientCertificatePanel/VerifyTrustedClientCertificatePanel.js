import React, { useState, useMemo } from "react";
import { render } from "react-dom";
import Styles from "./Style";
import { useForm, useField } from "react-final-form-hooks";
import Stack from "@mui/material/Stack";
import {Input} from "@material-ui/core";
import Button from "@mui/material/Button";

const VerifyTrustedCertificate = () => {
    const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));

    const onSubmit = async () => {
        await sleep(300);
        const url = process.env.REACT_APP_GATEWAY_URL + "/api/v1/certificate-service/certificate/upload";

        fetch(url, {
            method: 'POST',
            mode: 'cors',
            body: JSON.stringify({
                url: url.input.value,
                certificate: certificate.input.value
            })
        }).then((response) => {
            if (response.ok) {
                console.log(response);
                alert("Certificate added to the truststore!");
            } else if (!response.ok) {
                throw Error(response.statusText);
            }
        }).catch((error) => {
            console.log(error)
            alert(error.message);
            // setErrors(error.message)
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
            url: "",
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
            <h2> Verify trusted certificate panel</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Service URL</label>
                    <input {...url.input} placeholder="Service URL" />
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
                {/*<div>*/}
                {/*    <Stack direction="row" alignItems="center" spacing={2}>*/}
                {/*        <label htmlFor="contained-button-file">*/}
                {/*            <Input accept="image/*" id="contained-button-file" multiple type="file" />*/}
                {/*            <Button variant="contained" component="span">*/}
                {/*                Provide base64 encoded certificate*/}
                {/*            </Button>*/}
                {/*        </label>*/}
                {/*    </Stack>*/}
                {/*</div>*/}

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



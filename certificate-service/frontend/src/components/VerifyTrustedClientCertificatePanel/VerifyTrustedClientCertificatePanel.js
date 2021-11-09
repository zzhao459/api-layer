import React, { useState, useMemo } from "react";
import Styles from "./Style";
import { useForm, useField } from "react-final-form-hooks";
import FileUpload from "../FileUploadBar/FileUpload";

const VerifyTrustedCertificate = () => {
    const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));
    const [alias, setAlias] = useState(null);
    const onSubmit = async (data) => {

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

    const { form, handleSubmit, pristine, submitting} = useForm({
        onSubmit,
        initialValues,
        validate
    });

    const url = useField("url", form);

    return (
        <div>
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
            </form>
        </Styles>
        </div>
    );

}

export default VerifyTrustedCertificate;


